package org.stepinto.carnivore.sematics;
import java.util.*;
import org.stepinto.carnivore.common.*;
import org.stepinto.carnivore.parser.*;
import org.stepinto.carnivore.arch.*;

public class TypeChecker {
	public TypeChecker(SyntaxTree syntaxTree, ErrorManager errorMgr, Arch arch, boolean debugMode) {
		this(syntaxTree, errorMgr, arch);
		this.debugMode = debugMode;
	}

	public TypeChecker(SyntaxTree syntaxTree, ErrorManager errorMgr, Arch arch) {
		this.root = syntaxTree;
		this.errMgr = errorMgr;
		this.arch = arch;

		// insert int and string to tenv
		tenv.put(IntType.getInstance());
		tenv.put(StringType.getInstance());

		// init the main function
		mainFunc = new Function("main", new ArrayList<Variable>(), NoType.getInstance(),
				(Exp)syntaxTree, arch.newFrame(null, null));
		frames.push(mainFunc.getFrame());

		// add runtime functions into symbol-table
		for (Function func: RuntimeFunctions.getList())
			putVarOrFunc(func);
	}

	private boolean debugMode = false;

	private void debug(String msg) {
		if (debugMode)
			System.out.println(msg);
	}

	private void error(SematicException ex) {
		errMgr.report(ex.getLineNo(), ex.getMessage());
	}

	public void check() {
		try {
			checkExp((Exp)root);
		} catch (SematicException ex) {
			error(ex);
		}
	}

	private void checkAssignExp(AssignExp exp) throws SematicException {
		Exp left = exp.getLeft(), right = exp.getRight();
		checkExp(left);
		checkExp(right);
		Type leftType = getExpType(left), rightType = getExpType(right);

		if (left instanceof IdExp || left instanceof ArrayAccessExp || left instanceof RecordAccessExp) {
			// left is lvalue in this branch
			// check whether left and right have same type
			verifySameType(exp.getLineNo(), leftType, rightType);
			putExpType(exp, NoType.getInstance());
		}
		else
			error(exp.getLineNo(), "Expect lvalue.");
	}

	private void checkTypeDecl(TypeDecl decl) throws SematicException {
		if (decl instanceof TypeAliasDecl)
			checkTypeAliasDecl((TypeAliasDecl)decl);
		else if (decl instanceof ArrayDecl)
			checkArrayDecl((ArrayDecl)decl);
		else if (decl instanceof RecordDecl)
			checkRecordDecl((RecordDecl)decl);
		else
			assert(false);
	}

	private void checkDeclList(DeclList decls) throws SematicException {
		List<List<Decl>> declParts = new ArrayList<List<Decl>>();
		Decl prev = null;
		List<Decl> currPart = new ArrayList<Decl>();

		// partition decls by its "type"
		for (Decl d : decls.getDecls()) {
			boolean sameType = false;
			if (prev != null) {
				if (prev instanceof FuncDecl && d instanceof FuncDecl)
					sameType = true;
				if (prev instanceof VarDecl && d instanceof VarDecl)
					sameType = true;
				if (prev instanceof TypeDecl && d instanceof TypeDecl)
					sameType = true;
			}
			if (sameType)
				currPart.add(d);
			else {
				if (!currPart.isEmpty()) {
					declParts.add(currPart);
					currPart = new ArrayList<Decl>();
				}
				currPart.add(d);
			}
			prev = d;
		}
		if (!currPart.isEmpty())
			declParts.add(currPart);

		// check for each decl parts
		for (List<Decl> part : declParts) {
			assert(!part.isEmpty());
			if (part.get(0) instanceof FuncDecl) {
				for (Decl d: part)
					checkFuncDeclHeader((FuncDecl)d);
				for (Decl d: part)
					checkFuncDeclBody((FuncDecl)d);
			}
			if (part.get(0) instanceof VarDecl) {
				for (Decl d: part)
					checkVarDecl((VarDecl)d);
			}
			if (part.get(0) instanceof TypeDecl) {
				for (Decl d: part) 
					checkTypeDecl((TypeDecl)d);
			}
		}
	}

	private void checkLetExp(LetExp exp) throws SematicException {
		enterScope();
		try {
			checkDeclList(exp.getDecls());
			checkExp(exp.getBody());
		}
		catch (SematicException ex) {
			throw ex;  // re-throw this exception
		}
		finally {
			leaveScope();
		}

	}

	private void checkOpExp(OpExp exp) throws SematicException {
		Exp left = exp.getLeft(), right = exp.getRight();
		int op = exp.getOp();
		checkExp(left);
		checkExp(right);
		Type leftType = getExpType(left);
		Type rightType = getExpType(right);

		switch (op) {
		case OpExp.PLUS:
		case OpExp.MINUS:
		case OpExp.TIMES:
		case OpExp.DIVIDE:
			verifyIntType(left.getLineNo(), leftType);
			verifyIntType(right.getLineNo(), rightType);
			break;
		case OpExp.EQ:
		case OpExp.NEQ:
			verifySameType(right.getLineNo(), leftType, rightType);
			break;
		case OpExp.LT:
		case OpExp.LE:
		case OpExp.GE:
		case OpExp.GT:
			verifyPrimitiveType(left.getLineNo(), leftType);
			verifyPrimitiveType(right.getLineNo(), rightType);
			break;
		}

		putExpType(exp, getType("int"));
	}

	private void checkIdExp(IdExp exp) throws SematicException {
		String name = exp.getId();
		int line = exp.getLineNo();
		verifyNeedVarOrFuncId(line, name);
		if (getVar(name) != null) {
			Variable var = getVar(name);
			putExpType(exp, var.getType());
			putExpId(exp, var);

			// mark if the variable don't belong to the current frame
			if (getCurrFrame() != var.getFrame())
				var.setEscape(true);
		}
		else {
			error(line, "Expect a variable, but a function is found.");
			// Function func = getFunc(name);
			// putExpType(exp, func.getRetType());
			// putExpId(exp, func);
		}
	}

	private void checkIntExp(IntExp exp) throws SematicException {
		putExpType(exp, getType("int"));
	}

	private void checkStringExp(StringExp exp) throws SematicException {
		putExpType(exp, getType("string"));
	}

	private void checkArrayInitExp(ArrayInitExp exp) throws SematicException {
		int line = exp.getLineNo();
		String typeId = exp.getTypeId();
		verifyArrayTypeId(line, typeId);

		Exp sizeExp = exp.getSizeExp();
		checkExp(sizeExp);
		verifyIntType(sizeExp.getLineNo(), getExpType(sizeExp));

		ArrayType arrayType = (ArrayType)getType(typeId);
		Exp elemInitExp = exp.getElemInitExp();
		checkExp(elemInitExp);
		verifySameType(elemInitExp.getLineNo(), arrayType.getElemType(), getExpType(elemInitExp));

		putExpType(exp, arrayType);
	}

	private void checkRecordInitExp(RecordInitExp exp) throws SematicException {
		int line = exp.getLineNo();
		String typeId = exp.getId();
		verifyRecordTypeId(line, typeId);
		RecordType type = (RecordType)getType(typeId);

		List<FieldInitExp> initExps = exp.getInitExps().getExps();
		if (initExps.size() < type.getFields().size())
			error(line, "Expect more initializing expressions.");
		for (FieldInitExp initExp : initExps) {
			String fieldName = initExp.getId();
			Exp valueExp = initExp.getExp();

			if (!type.getFields().containsKey(fieldName))
				error(line, "Expect field name, but " + fieldName + " is found.");
			Type fieldType = type.getFields().get(fieldName);
			checkExp(valueExp);
			verifySameType(valueExp.getLineNo(), fieldType, getExpType(valueExp));
		}

		putExpType(exp, type);
	}

	private void checkArrayAccessExp(ArrayAccessExp exp) throws SematicException {
		Exp array = exp.getId(), index = exp.getIndexExp();
		int line = exp.getLineNo();
		checkExp(array);
		checkExp(index);
		verifyIntType(index.getLineNo(), getExpType(index));
		verifyArrayType(array.getLineNo(), getExpType(array));

		ArrayType arrayType = (ArrayType)getExpType(array);
		putExpType(exp, arrayType.getElemType());
	}

	private void checkRecordAccessExp(RecordAccessExp exp) throws SematicException {
		Exp record = exp.getId();
	       	String fieldName = exp.getFieldId();
		int line = exp.getLineNo();

		checkExp(record);
		verifyRecordType(record.getLineNo(), getExpType(record));
		RecordType recType = (RecordType)getExpType(record);
		if (recType.getFields().containsKey(fieldName)) {
			Type type = recType.getFields().get(fieldName);
			putExpType(exp, type);
		}
		else {
			error(line, "Record type " + recType.getName()
					+ " does not have a field naming " + fieldName + ".");
		}
	}

	private void checkFuncCallExp(FuncCallExp exp) throws SematicException {
		int line = exp.getLineNo();
		Exp nameExp = exp.getId();
		ArrayList<Exp> argExps = new ArrayList<Exp>(exp.getArgs().getArgs());
		
		// verify name
		if (!(nameExp instanceof IdExp))
			error(line, "Expect function name.");
		String name = ((IdExp)nameExp).getId();
		verifyFuncId(line, name);
		Function func = getFunc(name);
		putExpId((IdExp)nameExp, func);

		// verify arguments
		ArrayList<Variable> args = new ArrayList<Variable>(func.getArgs());
		if (args.size() == argExps.size()) {
			for (int i = 0; i < args.size(); i++) {
				Exp argExp = argExps.get(i);
				checkExp(argExp);
				verifySameType(argExp.getLineNo(), args.get(i).getType(), getExpType(argExp));
			}
			putExpType(exp, func.getRetType());
		}
		else
			error(line, "Less or more arguments are required.");
	}

	private void checkIfExp(IfExp exp) throws SematicException {
		Exp condi = exp.getCondiExp();
		Exp thenBody = exp.getThenBody();
		Exp elseBody = exp.getElseBody();
		int line = exp.getLineNo();

		checkExp(condi);
		verifyIntType(condi.getLineNo(), getExpType(condi));

		if (elseBody == null) {
			checkExp(thenBody);
			Type thenType = getExpType(thenBody);
			putExpType(exp, thenType);
		}
		else {
			checkExp(thenBody);
			checkExp(elseBody);
			Type thenType = getExpType(thenBody);
			Type elseType = getExpType(elseBody);
			verifySameType(elseBody.getLineNo(), thenType, elseType);
			putExpType(exp, thenType);
		}
	}

	private void checkWhileExp(WhileExp exp) throws SematicException {
		int line = exp.getLineNo();
		Exp condi = exp.getCondiExp();
		Exp body = exp.getBody();

		checkExp(condi);
		enterLoop();
		checkExp(body);
		leaveLoop();
		verifyIntType(condi.getLineNo(), getExpType(condi));
		// verifyNoType(getExpType(body));
	}

	private void checkForExp(ForExp exp) throws SematicException {
		Exp from = exp.getFromExp(), to = exp.getToExp(), body = exp.getBody();
		String varId = exp.getVarId();
		int line = exp.getLineNo();

		enterScope();
		try {
			Variable var = new Variable(varId, getType("int"), from, false, getCurrFrame());
			putLocalVarToFrame(var);
			putVarOrFunc(var);
			putDeclId(exp, var);

			checkExp(from);
			checkExp(to);
			enterLoop();
			checkExp(body);
			leaveLoop();
			verifyIntType(from.getLineNo(), getExpType(from));
			verifyIntType(to.getLineNo(), getExpType(to));
		}
		catch (SematicException ex) {
			throw ex;
		}
		finally {
			leaveScope();
		}
	}

	private void checkCompExpList(CompExpList exp) throws SematicException {
		Type lastType = NoType.getInstance();
		for (Exp e : exp.getExps()) {
			checkExp(e);
			lastType = getExpType(e);
		}
		putExpType(exp, lastType);
	}

	private void checkNilExp(Exp exp) throws SematicException {
		putExpType(exp, NilType.getInstance());
	}

	private void checkBreakExp(BreakExp exp) throws SematicException {
		if (!inLoop())
			error(exp.getLineNo(), "Illegal break outside loops.");
	}

	private void checkExp(Exp exp) throws SematicException {
		if (exp instanceof AssignExp) 
			checkAssignExp((AssignExp)exp);
		else if (exp instanceof LetExp) 
			checkLetExp((LetExp)exp);
		else if (exp instanceof CompExpList) 
			checkCompExpList((CompExpList)exp);
		else if (exp instanceof OpExp) 
			checkOpExp((OpExp)exp);
		else if (exp instanceof IdExp) 
			checkIdExp((IdExp)exp);
		else if (exp instanceof IntExp)
			checkIntExp((IntExp)exp);
		else if (exp instanceof StringExp)
			checkStringExp((StringExp)exp);
		else if (exp instanceof ArrayInitExp)
			checkArrayInitExp((ArrayInitExp)exp);
		else if (exp instanceof RecordInitExp)
			checkRecordInitExp((RecordInitExp)exp);
		else if (exp instanceof ArrayAccessExp)
			checkArrayAccessExp((ArrayAccessExp)exp);
		else if (exp instanceof RecordAccessExp)
			checkRecordAccessExp((RecordAccessExp)exp);
		else if (exp instanceof FuncCallExp)
			checkFuncCallExp((FuncCallExp)exp);
		else if (exp instanceof ForExp)
			checkForExp((ForExp)exp);
		else if (exp instanceof WhileExp)
			checkWhileExp((WhileExp)exp);
		else if (exp instanceof IfExp)
			checkIfExp((IfExp)exp);
		else if (exp instanceof NilExp)
			checkNilExp((NilExp)exp);
		else if (exp instanceof BreakExp)
			checkBreakExp((BreakExp)exp);
		else
			assert(false);
	}

	private void checkTypeAliasDecl(TypeAliasDecl decl) throws SematicException {
		String alias = decl.getAlias();
		String orgTypeId = decl.getRealName();
		Type orgType = getType(orgTypeId);
		int line = decl.getLineNo();

		// verify the original should exist and no duplicated type with same name as the new alias defined
		verifyTypeId(line, orgTypeId);
		verifyNoLocalTypeId(line, alias);

		// put the type alias into symbol-table and decl-id table
		TypeAlias typeAlias = new TypeAlias(alias, orgType);
		putType(typeAlias);
		putDeclId(decl, typeAlias);

		// leave a message that we've received a type alias
		debug("type-alias decl: " + alias + " = " + orgTypeId);
	}

	private void checkArrayDecl(ArrayDecl decl) throws SematicException {
		String elemTypeId = decl.getElemTypeId();
		String name = decl.getId();
		int line = decl.getLineNo();
		Type elemType = getType(elemTypeId);

		// verify elemTypeId is a type id and no local defined type has same name of this decl.
		verifyTypeId(line, elemTypeId);
		verifyNoLocalTypeId(line, name);

		// put the type decl. into symbol table and decl-id table
		ArrayType arrayType = new ArrayType(name, elemType);
		putType(arrayType);
		putDeclId(decl, arrayType);

		// leave a message that we've got a array decl.
		debug("array type decl: " + name);
	}

	private void checkRecordDecl(RecordDecl decl) throws SematicException {
		String name = decl.getId();
		int line = decl.getLineNo();

		// verify no local duplicated decl.
		verifyNoLocalTypeId(line, name);

		// for each fields, find it's type and do validation meanwhile
		List<TypeField> fieldDecls = decl.getFields().getFields();
		Map<String, Type> fields = new HashMap<String, Type>();
		for (TypeField fd: fieldDecls) {
			String typeId = fd.getTypeId();
			if (!typeId.equals(name))
				verifyTypeId(line, typeId);
		}

		// create a type and put it into the symbol table, then add fields
		RecordType recordType = new RecordType(name, fields);
		putType(recordType);
		putDeclId(decl, recordType);
		for (TypeField fd: fieldDecls) {
			String varId = fd.getVarId();
			String typeId = fd.getTypeId();
			fields.put(varId, getType(typeId));
		}
		recordType.setFields(fields);

		// leave a debug message
		debug("record type decl: " + name);
	}

	private void checkVarDecl(VarDecl decl) throws SematicException {
		int line = decl.getLineNo();
		String varId = decl.getVarId();
		String typeId = decl.getTypeId();

		verifyNoLocalVarId(line, varId);

		// check if the init-exp type is as same as type-id
		Exp initExp = decl.getInitExp();
		checkExp(initExp);
		verifyExpHasType(initExp);
		Type initExpType = getExpType(initExp);
		if (typeId != null) {
			verifyTypeId(line, typeId);
			verifySameType(initExp.getLineNo(), getType(typeId), initExpType);
		}

		// initExpType must not be NoType
		// i.e. var a := nil is forbiden
		if (typeId == null && initExpType instanceof NilType)
			error(initExp.getLineNo(), "Cannot determine type from initializor.");

		// put it in the symbol-table and the decl-id table
		Variable var = new Variable(varId, initExpType, initExp, false, getCurrFrame());
		putVarOrFunc(var);
		putDeclId(decl, var);
		putLocalVarToFrame(var);

		// print debug msg that we've got a variable declared
		debug("var decl: " + varId + " of type " + initExpType.getName());
	}

	private void checkFuncDeclHeader(FuncDecl decl) throws SematicException {
		int line = decl.getLineNo();
		String name = decl.getId();
		String retTypeId = decl.getRetId();
		Exp body = decl.getBody();
		List<TypeField> fields = decl.getArgs().getFields();
		Type retType = null;

		verifyNoLocalFuncId(line, name);

		if (decl.getRetId() != null) {
			// it's a function
			verifyTypeId(line, retTypeId);
			retType = getType(retTypeId);
		}
		else
			retType = NoType.getInstance();
		
		// check arg types
		List<Variable> args = new ArrayList<Variable>();
		for (TypeField f : fields) {
			verifyTypeId(f.getLineNo(), f.getTypeId());
			args.add(new Variable(f.getVarId(), getType(f.getTypeId()), false, null));
		}

		// put it in the symbol-table and exp-id table
		Variable staticLink = new Variable("@static_link", IntType.getInstance(),
				null, false, getCurrFrame());
		Frame frame = arch.newFrame(getCurrFrame(), staticLink);
		Function func = new Function(name, args, retType, body, frame);
		putVarOrFunc(func);
		putDeclId(decl, func);
		debug("func decl: " + name);
	}

	private void checkFuncDeclBody(FuncDecl decl) throws SematicException {
		String name = decl.getId();
		Function func = getFunc(name);
		Exp body = func.getBody();

		enterScope();
		enterFunc(func.getFrame());
		try {
			// add each parameters into symbol tabel and frame 
			for (Variable arg : func.getArgs()) {
				putVarOrFunc(arg);
				putParamVarToFrame(arg);
			}
			checkExp(body);
		}
		catch (SematicException ex) {
			throw ex;
		}
		finally {
			leaveScope();
			leaveFunc();
		}
	}

	private Type getType(String name) {
		Type type = (Type)tenv.lookup(name);
		if (type instanceof TypeAlias)
			return ((TypeAlias)type).getActualType();
		else
			return type;
	}

	private Type getTypeLocal(String name) {
		Type type = (Type)tenv.lookupLocal(name);
		if (type instanceof TypeAlias)
			return ((TypeAlias)type).getActualType();
		else
			return type;
	}

	private Variable getVarLocal(String name) {
		Identifier id = venv.lookupLocal(name);
		if (id instanceof Variable)
			return (Variable)id;
		else
			return null;
	}

	private Function getFuncLocal(String name) {
		Identifier id = venv.lookupLocal(name);
		if (id instanceof Function)
			return (Function)id;
		else
			return null;
	}

	private void putType(Type type) {
		tenv.put(type);
	}

	private Variable getVar(String name) {
		Identifier id = venv.lookup(name);
		if (id instanceof Variable)
			return (Variable)id;
		else
			return null;
	}

	private Function getFunc(String name) {
		Identifier id = venv.lookup(name);
		if (id instanceof Function)
			return (Function)id;
		else
			return null;
	}

	private void error(int lineNo, String msg) throws SematicException {
		throw new SematicException(lineNo, msg);
	}

	private void verifyArrayTypeId(int line, String typeId) throws SematicException {
		verifyTypeId(line, typeId);
		Type type = getType(typeId);
		if (!(type instanceof ArrayType))
			error(line, "Expect array type name, but " + typeId + " is found.");
	}

	private void verifyNoLocalTypeId(int line, String name) throws SematicException {
		Type type = getTypeLocal(name);
		if (type != null)
			error(line, "Redeclaration of type " + name + ".");
	}

	private void verifyNoLocalVarId(int line, String name) throws SematicException {
		Variable var = getVarLocal(name);
		if (var != null)
			error(line, "Redeclaration of variable " + name + ".");
	}

	private void verifyTypeId(int line, String name) throws SematicException {
		Type type = getType(name);
		if (type == null)
			error(line, "Expect type name, but " + name + " is found.");
	}

	private void verifySameType(int line, Type a, Type b) throws SematicException {
		if (!a.equals(b))
			error(line, "Expect same types, but " + a.getName() + " and "
					+ b.getName() + " are found.");
	}

	private void verifyFuncId(int line, String name) throws SematicException {
		if (getFunc(name) == null)
			error(line, "Expect function name, but " + name + " is found.");
	}

	private void verifyExpHasType(Exp exp) throws SematicException {
		Type type = getExpType(exp);
		if (type == null || type instanceof NoType)
			error(exp.getLineNo(), "Expect a typed expression.");
	}

	private void verifyNeedVarOrFuncId(int line, String id) throws SematicException {
		if (getVar(id) == null && getFunc(id) == null)
			error(line, "Expect a variable or function name, but " + id + " is found.");
	}

	private void verifyIntType(int line, Type type) throws SematicException {
		if (!(type instanceof IntType)) 
			error(line, "Expect an integer expression or variable, but " + type.getName() + " is found.");
	}

	private void verifyPrimitiveType(int line, Type type) throws SematicException {
		if (!(type instanceof IntType) && !(type instanceof StringType))
			error(line, "Expect a primitive expression or variable, but " + type.getName() + " is found.");
	}

	private void verifyArrayType(int line, Type type) throws SematicException {
		if (!(type instanceof ArrayType))
			error(line, "Expect an array expression or variable.");
	}

	private void verifyRecordType(int line, Type type) throws SematicException {
		if (!(type instanceof RecordType))
			error(line, "Expect a record expression or variable.");
	}

	private void verifyRecordTypeId(int line, String typeId) throws SematicException {
		Type type = getType(typeId);
		if (type == null || !(type instanceof RecordType))
			error(line, "Expect a record name, but " + typeId + " is found.");
	}

	public void verifyNoLocalFuncId(int line, String typeId) throws SematicException {
		if (getFuncLocal(typeId) != null)
			error(line, "Redeclaration of function " + typeId + ".");
	}

	private void putVarOrFunc(Identifier id) {
		venv.put(id);
	}

	private Type getExpType(Exp exp) {
		Type type = expTypes.get(exp);
		if (type == null)
			return NoType.getInstance();
		else
			return type;
	}

	public Map<Exp, Type> getExpTypeTable() {
		return expTypes;
	}

	private void putExpType(Exp exp, Type type) {
		expTypes.put(exp, type);
	}

	private void enterScope() {
		tenv.push();
		venv.push();
	}

	private void leaveScope() {
		tenv.pop();
		venv.pop();
	}

	public Map<SyntaxTree, Identifier> getDeclIdTable() {
		return declIds;
	}

	public Map<IdExp, Identifier> getExpIdTable() {
		return expIds;
	}

	private void putDeclId(SyntaxTree decl, Identifier id) {
		declIds.put(decl, id);
	}
	
	private void putExpId(IdExp exp, Identifier id) {
		expIds.put(exp, id);
	}

	// return the current frame (i.e. inner-most frame)
	private Frame getCurrFrame() {
		return frames.peek();
	}

	// create a new frame. it is called entering a function body
	private void enterFunc(Frame frame) {
		frames.push(frame);
	}

	// removes the inner-most frame. it is called leaving a function body
	private void leaveFunc() {
		frames.pop();
	}

	// add local-var to the current frame
	private void putLocalVarToFrame(Variable var) {
		getCurrFrame().addLocal(var);
	}

	private void putParamVarToFrame(Variable var) {
		getCurrFrame().addParam(var);
	}

	// return main()
	public Function getEntryFunc() {
		return mainFunc;
	}

	private boolean inLoop() {
		return loopCount > 0;
	}

	private void enterLoop() {
		loopCount++;
	}

	private void leaveLoop() {
		loopCount--;
	}

	private int loopCount;

	private SyntaxTree root;
	private ErrorManager errMgr;

	private SymbolTable tenv = new SymbolTable();
	private SymbolTable venv = new SymbolTable();

	private Map<Exp, Type> expTypes = new HashMap<Exp, Type>();

	// a stack of frames, which helps to determine variable escapes
	private Stack<Frame> frames = new Stack<Frame>();

	// one exception for this is FOR loop, where variable are declared in
	// a expression rather than declaration. so we use syntaxtree.
	private Map<SyntaxTree, Identifier> declIds = new HashMap<SyntaxTree, Identifier>();
	private Map<IdExp, Identifier> expIds = new HashMap<IdExp, Identifier>();

	// main function
	// it is automatically created to encapsulate the out-most frame and
	// its variables
	// it is intialized in the constructor
	private Function mainFunc = null;

	// arch
	// might be instance of IntelArch or sth else
	private Arch arch;
}

