package org.stepinto.carnivore.ir;

import java.io.*;
import java.util.*;
import org.stepinto.carnivore.parser.*;
import org.stepinto.carnivore.sematics.*;
import org.stepinto.carnivore.arch.*;

public class Translator {
	public static final String FRAME_POINTER_NAME = "frame_ptr";
	public static final String STATIC_LINK_VAR_NAME = "static_link";
	private static final String USER_VAR_PREFIX = "v";
	private static final String TEMP_VAR_PREFIX = "t";
	private static final String USER_FUNC_PREFIX = "F";

	public Translator(/*SyntaxTree syntaxTree,*/ Map<Exp, Type> expTypes, 
			Map<SyntaxTree, Identifier> declIds, Map<IdExp, Identifier> expIds,
			Function entryFunc, Arch arch) {
		// this.syntaxTree = syntaxTree;
		this.expTypes = expTypes;
		this.declIds = declIds;
		this.expIds = expIds;
		this.entryFunc = entryFunc;
		this.arch = arch;
	}

	public void translate() {
		translateFunc(entryFunc);
	}

	private String userVarName(int id) {
		return USER_VAR_PREFIX + id;
	}

	private String userVarName(Variable var) {
		return userVarName(var.getUid());
	}

	private String userVarName(IdExp exp) {
		return userVarName((Variable)getExpId(exp));
	}

	private void translateAssignExp(InsBuffer ibuf, AssignExp exp, Frame frame) {
		Exp rightExp = exp.getRight();
		translateExp(ibuf, rightExp, frame);
		String tempVar = getExpVar(rightExp);
		
		Exp leftExp = exp.getLeft();
		if (leftExp instanceof IdExp) {
			Variable leftVar = (Variable)getExpId((IdExp)leftExp);
			if (frame.contains(leftVar))
				ibuf.writeAssignIns(userVarName(leftVar), tempVar);
			else {
				String staticLink = userVarName(frame.getStaticLink());
				while (frame != null && !frame.contains(leftVar)) {
					String tmp = newTempVar();
					int offset = frame.getOffset(frame.getStaticLink());
					ibuf.writeMemReadIns(tmp, staticLink, String.valueOf(offset));
					staticLink = tmp;
					frame = frame.getParent();
				}

				ibuf.writeMemWriteIns(staticLink, String.valueOf(frame.getOffset(leftVar)), tempVar);
			}
		}	
		else if (leftExp instanceof ArrayAccessExp) {
			Exp idExp = ((ArrayAccessExp)leftExp).getId();
			Exp indexExp = ((ArrayAccessExp)leftExp).getIndexExp();
			translateExp(ibuf, idExp, frame);
			translateExp(ibuf, indexExp, frame);

			int elemSize = getTypeSize(getExpType(leftExp));
			String base = getExpVar(idExp);
			String index = getExpVar(indexExp);
			String tmp = newTempVar();
			ibuf.writeArthIns(ArthIns.TIMES, tmp, index, String.valueOf(elemSize));
			ibuf.writeMemWriteIns(base, tmp, tempVar);
		}
		else if (leftExp instanceof RecordAccessExp) {
			Exp idExp = ((RecordAccessExp)leftExp).getId();
			String field = ((RecordAccessExp)leftExp).getFieldId();
			RecordType recType = (RecordType)getExpType(idExp);
			int offset = getFieldOffset(recType, field);
			translateExp(ibuf, idExp, frame);
			String base = getExpVar(idExp);
			ibuf.writeMemWriteIns(base, String.valueOf(offset), tempVar);
		}
		else
			assert(false);
	}


	// translate the function body into intermedian code if neccessary
	// if either of the following situation happens, that function will not be
	// translated:
	// a) it's a runtime function
	// b) it has been translated
	private void translateFunc(Function func) {
		if (!funcIns.containsKey(func) && !RuntimeFunctions.isRuntimeFunc(func))
			doTranslateFunc(func);
	}

	private void doTranslateFunc(Function func) {
		Frame frame = func.getFrame();
		InsBuffer ibuf = new InsBuffer();
		funcIns.put(func, ibuf);

		// if this function is not a runtime function or main()
		// the parameter at the top of the stack should be the
		// pointer of the parent frame
		if (frame.getStaticLink() != null)
			ibuf.writeVarDeclIns(userVarName(frame.getStaticLink()), false);

		for (Variable var: frame.getParams())
			ibuf.writeVarDeclIns(userVarName(var), false); // decl param var
		for (Variable var: frame.getLocals())
			ibuf.writeVarDeclIns(userVarName(var), true);  // decl local var

		// tranlsate init-exps
		for (Variable var: frame.getLocals()) {
			Exp initExp = var.getInitExp();
			translateExp(ibuf, initExp, frame);
			ibuf.writeAssignIns(userVarName(var), getExpVar(initExp));
		}

		Exp body = func.getBody();
		translateExp(ibuf, body, frame);
		if (func.getRetType() instanceof NoType)
			ibuf.writeRetIns();
		else
			ibuf.writeRetIns(getExpVar(body));
	}


	private void translateLetExp(InsBuffer ibuf, LetExp exp, Frame frame) {
		translateExp(ibuf, exp.getBody(), frame);
	}

	private void translateCompExpList(InsBuffer ibuf, CompExpList exp, Frame frame) {
		Exp last = null;
		for (Exp e : exp.getExps()) {
			translateExp(ibuf, e, frame);
			last = e;
		}
		if (last != null && !(getExpType(exp) instanceof NoType))
			putExpVar(exp, getExpVar(last));
	}

	private void translateOpExp(InsBuffer ibuf, OpExp exp, Frame frame) {
		Exp leftExp = exp.getLeft(), rightExp = exp.getRight();
		translateExp(ibuf, leftExp, frame);
		translateExp(ibuf, rightExp, frame);

		String leftVar = getExpVar(leftExp);
		String rightVar = getExpVar(rightExp);
		String result = newTempVar();
		switch (exp.getOp()) {
		case OpExp.PLUS:
			ibuf.writeArthIns(ArthIns.PLUS, result, leftVar, rightVar);
			break;
		case OpExp.MINUS:
			ibuf.writeArthIns(ArthIns.MINUS, result, leftVar, rightVar);
			break;
		case OpExp.TIMES:
			ibuf.writeArthIns(ArthIns.TIMES, result, leftVar, rightVar);
			break;
		case OpExp.DIVIDE:
			ibuf.writeArthIns(ArthIns.DIVIDE, result, leftVar, rightVar);
			break;
		case OpExp.EQ:
		case OpExp.NEQ:
		case OpExp.LT:
		case OpExp.LE:
		case OpExp.GT:
		case OpExp.GE:
			// 0: if left < right then jump 3
			// 1: result := 0
			// 2: jump 4
			// 3: result := 1
			// 4: nop
			int relOp = 0;
			if (exp.getOp() == OpExp.EQ) relOp = JumpIfIns.EQ;
			if (exp.getOp() == OpExp.NEQ) relOp = JumpIfIns.NEQ;
			if (exp.getOp() == OpExp.LT) relOp = JumpIfIns.LT;
			if (exp.getOp() == OpExp.LE) relOp = JumpIfIns.LE;
			if (exp.getOp() == OpExp.GT) relOp = JumpIfIns.GT;
			if (exp.getOp() == OpExp.GE) relOp = JumpIfIns.GE;

			// TODO: call strcmp() if the two variables are string

			ibuf.writeJumpIfIns(relOp, leftVar, rightVar, ibuf.getCurrLineNo()+3);
			ibuf.writeAssignIns(result, "0");
			ibuf.writeJumpIns(ibuf.getCurrLineNo() + 2);
			ibuf.writeAssignIns(result, "1");
			ibuf.writeNopIns();
			break;
		}
		putExpVar(exp, result);
	}

	private void translateIdExp(InsBuffer ibuf, IdExp exp, Frame frame) {
		// if the variable does not exist in the current frame,
		// we need to use static-link to access it
		Variable var = (Variable)getExpId(exp);
		if (frame.contains(var))
			putExpVar(exp, userVarName(var));
		else {
			String staticLink = userVarName(frame.getStaticLink());
			while (frame != null && !frame.contains(var)) {
				String tmp = newTempVar();
				ibuf.writeMemReadIns(tmp, staticLink,
						String.valueOf(frame.getOffset(frame.getStaticLink())));
				staticLink = tmp;
				frame = frame.getParent();
			}

			String ret = newTempVar();
			ibuf.writeMemReadIns(ret, staticLink, String.valueOf(frame.getOffset(var)));
			putExpVar(exp, ret);
		}
	}

	private void translateIntExp(InsBuffer ibuf, IntExp exp, Frame frame) {
		int value = exp.getValue();
		putExpVar(exp, String.valueOf(value));
	}

	private void translateStringExp(InsBuffer ibuf, StringExp exp, Frame frame) {
		String value = exp.getValue();
		int strId = getStringId(value);
		String param = newTempVar();
		String result = newTempVar();
		ibuf.writeAssignIns(param, String.valueOf(strId));
		ibuf.writeParamIns(param);
		ibuf.writeCallIns(result, "_getstr");
		putExpVar(exp, result);
	}

	private void translateArrayInitExp(InsBuffer ibuf, ArrayInitExp exp, Frame frame) {
		// result := intArray [N+N] of 2*i
		// -->
		// elem_size := ...
		// array_size := ...
		// init_value := ...
		// total_size := elem_size * array_size
		// param total_size
		// result := call _malloc
		// 0: t0 := 0
		// 1: if t0<array_size then goto 3
		// 2: goto 7
		// 3: t1 = t0 * elem_size
		// 4: result[t1] := init_value
		// 5: t0 := t0 + 1
		// 6: goto 2
		// 7: nop
		translateExp(ibuf, exp.getSizeExp(), frame);
		translateExp(ibuf, exp.getElemInitExp(), frame);
		String sizeVar = getExpVar(exp.getSizeExp());
		String initVar = getExpVar(exp.getElemInitExp());
		int elemSize = getTypeSize(((ArrayType)getExpType(exp)).getElemType());
		String totalSizeVar = newTempVar();
		String t0 = newTempVar();
		String t1 = newTempVar();
		String result = newTempVar();

		ibuf.writeArthIns(ArthIns.TIMES, totalSizeVar, String.valueOf(elemSize), sizeVar);
		ibuf.writeParamIns(totalSizeVar);
		ibuf.writeCallIns(result, "_malloc");
		ibuf.writeAssignIns(t0, "0");
		ibuf.writeJumpIfIns(JumpIfIns.LT, t0, sizeVar, ibuf.getCurrLineNo() + 2);
		ibuf.writeJumpIns(ibuf.getCurrLineNo() + 5);
		ibuf.writeArthIns(ArthIns.TIMES, t1, t0, String.valueOf(elemSize));
		ibuf.writeMemWriteIns(result, t1,initVar);
		ibuf.writeArthIns(ArthIns.PLUS, t0, t0, "1");
		ibuf.writeJumpIns(ibuf.getCurrLineNo() - 4);
		ibuf.writeNopIns();

		putExpVar(exp, result);
	}

	private int getRecordSize(RecordType recType) {
		int size = 0;
		for (Type type: recType.getFields().values())
			size += getTypeSize(type);
		return size;
	}

	private void translateRecordInitExp(InsBuffer ibuf, RecordInitExp exp, Frame frame) {
		// ret := recName { x = x0, y = y0, z = z0 }
		// -->
		// totalSize := ...
		// param totalSize
		// ret := call _malloc
		// ret[x_offset] := x0
		// ret[y_offset] := y0
		// ret[z_offset] := z0
		RecordType recType = (RecordType)getExpType(exp);
		int totalSize = getRecordSize(recType);
		String totalSizeVar = newTempVar();
		ibuf.writeAssignIns(totalSizeVar, String.valueOf(totalSize));

		String ret = newTempVar();
		ibuf.writeParamIns(totalSizeVar);
		ibuf.writeCallIns(ret, "_malloc");

		for (FieldInitExp initExp: exp.getInitExps().getExps()) {
			translateExp(ibuf, initExp.getExp(), frame);
			int offset = getFieldOffset(recType, initExp.getId());
			ibuf.writeMemWriteIns(ret, String.valueOf(offset),
					getExpVar(initExp.getExp()));
		}
		putExpVar(exp, ret);
	}

	private void translateArrayAccessExp(InsBuffer ibuf, ArrayAccessExp exp, Frame frame) {
		// ret := arr[idx]
		// -->
		// t0 := idx
		// t1 := arr*sizeof(type)
		// ret := t0[t1]
		Exp idExp = exp.getId();
		Exp indexExp = exp.getIndexExp();
		translateExp(ibuf, idExp, frame);
		translateExp(ibuf, indexExp, frame);

		// determine size of element of the array
		Type elemType = getExpType(exp);
		int elemSize = getTypeSize(elemType);

		String t0 = getExpVar(idExp);
		String t1 = newTempVar();
		ibuf.writeArthIns(ArthIns.TIMES, t1, getExpVar(indexExp), String.valueOf(elemSize));
		String ret = newTempVar();
		ibuf.writeMemReadIns(ret, t0, t1);
		putExpVar(exp, ret);
	}

	private int getFieldOffset(RecordType recType, String field) {
		int offset = 0;

		for (Map.Entry<String, Type> entry: recType.getFields().entrySet()) {
			if (entry.getKey().equals(field))
				return offset;
			offset += getTypeSize(entry.getValue());
		}
		assert(false);
		return -1;
	}

	private void translateRecordAccessExp(InsBuffer ibuf, RecordAccessExp exp, Frame frame) {
		// ret := rec.field
		// -->
		// t0 := rec
		// t1 := field_offset
		// ret := t0[t1]
		Exp idExp = exp.getId();
		String field = exp.getFieldId();
		RecordType recType = (RecordType)getExpType(idExp);
		int offset = getFieldOffset(recType, field);
		translateExp(ibuf, idExp, frame);

		String t0 = getExpVar(idExp);
		String ret = newTempVar();
		ibuf.writeMemReadIns(ret, t0, String.valueOf(offset));
		putExpVar(exp, ret);
	}

	private String userFuncName(Function func) {
		if (RuntimeFunctions.isRuntimeFunc(func))
			return func.getName();
		else
			return USER_FUNC_PREFIX + func.getUid();
	}

	private void translateFuncCallExp(InsBuffer ibuf, FuncCallExp exp, Frame frame) {
		Function func = (Function)getExpId((IdExp)exp.getId());
		Stack<String> paramTempVars = new Stack<String>();

		for (Exp argExp : exp.getArgs().getArgs()) {
			translateExp(ibuf, argExp, frame);
			paramTempVars.push(getExpVar(argExp));
		}
		while (!paramTempVars.isEmpty()) {
			ibuf.writeParamIns(paramTempVars.peek());
			paramTempVars.pop();
		}

		// all user defined functions except main() have a static-
		// link pointed to the the outter-level frame. it provides
		// access to variables defined in that frames. 
		//
		// we don't need to check if func == main(), since user
		// cannot call it.
		if (!RuntimeFunctions.isRuntimeFunc(func))
			ibuf.writeParamIns(FRAME_POINTER_NAME);

		String retVar = newTempVar();
		ibuf.writeCallIns(retVar, userFuncName(func));
		putExpVar(exp, retVar);

		// translate callee function if neccessary
		translateFunc(func);
	}

	private void translateForExp(InsBuffer ibuf, ForExp exp, Frame frame) {
		// for i = a to b do body
		//
		//     i := a
		// L1: t0 := b
		//     if i >= t0 then goto L2
		//     body
		//     t1 := i+1
		//     i := t1
		//     jump L1
		// L2: nop

		String iVar = userVarName((Variable)declIds.get(exp));
		Exp fromExp = exp.getFromExp();
		translateExp(ibuf, fromExp, frame);
		ibuf.writeAssignIns(iVar, getExpVar(fromExp));

		int l1 = ibuf.getCurrLineNo();
		String l2 = newLabel();
		Exp toExp = exp.getToExp();
		translateExp(ibuf, toExp, frame);
		ibuf.writeFakeJumpIfIns(JumpIfIns.GT, iVar, getExpVar(toExp), l2);

		setBreakToLabel(l2);
		translateExp(ibuf, exp.getBody(), frame);
		String t1 = newTempVar();
		ibuf.writeArthIns(ArthIns.PLUS, t1, iVar, "1");
		ibuf.writeAssignIns(iVar, t1);

		ibuf.writeJumpIns(l1);

		ibuf.patchFakeJump(l2, ibuf.getCurrLineNo());
		ibuf.writeNopIns();
	}

	private void translateWhileExp(InsBuffer ibuf, WhileExp exp, Frame frame) {
		// while condi do body
		//
		// L1: t0 := condi
		//     if t0 = 0 then goto L2
		//     body
		//     jump L1
		// L2: nop
		Exp condiExp = exp.getCondiExp();
		int l1 = ibuf.getCurrLineNo();
		String l2 = newLabel();
		translateExp(ibuf, condiExp, frame);
		ibuf.writeFakeJumpIfIns(JumpIfIns.EQ, getExpVar(condiExp), "0", l2);

		setBreakToLabel(l2);
		translateExp(ibuf, exp.getBody(), frame);
		ibuf.writeJumpIns(l1);

		ibuf.patchFakeJump(l2, ibuf.getCurrLineNo());
		ibuf.writeNopIns();
	}

	private void translateIfExp(InsBuffer ibuf, IfExp exp, Frame frame) {
		//     t0 := exp
		//     if t0 = 0 then goto L1
		//     <then-body>
		//     jump L2
		// L1: <else-body>
		// L2: nop
		Exp condiExp = exp.getCondiExp();
		String l1 = newLabel();
		String l2 = newLabel();
		String ret = newTempVar();
		translateExp(ibuf, condiExp, frame);
		ibuf.writeFakeJumpIfIns(JumpIfIns.EQ, getExpVar(condiExp), "0", l1);

		Exp thenBody = exp.getThenBody();
		translateExp(ibuf, thenBody, frame);
		ibuf.writeAssignIns(ret, getExpVar(thenBody));
		ibuf.writeFakeJumpIns(l2);

		Exp elseBody = exp.getElseBody();
		ibuf.patchFakeJump(l1, ibuf.getCurrLineNo());
		translateExp(ibuf, elseBody, frame);
		ibuf.writeAssignIns(ret, getExpVar(elseBody));

		ibuf.patchFakeJump(l2, ibuf.getCurrLineNo());
		ibuf.writeNopIns();

		putExpVar(exp, ret);
	}

	/*private void translateBoolExp(Exp exp, String trueLabel, String falseLabel) {
		boolean isRelOp = false;
		int relOp = 0;
		System.out.println(exp.getClass());
		if (exp instanceof OpExp) {
			OpExp opExp = (OpExp)exp;
			switch (opExp.getOp()) {
				case OpExp.EQ: isRelOp = true; relOp = JumpIfIns.EQ; break;
				case OpExp.NEQ: isRelOp = true; relOp = JumpIfIns.NEQ; break;
				case OpExp.LT: isRelOp = true; relOp = JumpIfIns.LT; break;
				case OpExp.LE: isRelOp = true; relOp = JumpIfIns.LE; break;
				case OpExp.GT: isRelOp = true; relOp = JumpIfIns.GT; break;
				case OpExp.GE: isRelOp = true; relOp = JumpIfIns.GE; break;
			}
		}


		if (isRelOp) {
			// if left op right then goto true
			// goto false
			Exp leftExp = ((OpExp)exp).getLeft();
			Exp rightExp = ((OpExp)exp).getRight();
			translateExp(leftExp);
			translateExp(rightExp);

			String leftVar = getExpVar(leftExp);
			String rightVar = getExpVar(rightExp);
			writeFakeJumpIfIns(relOp, leftVar, rightVar, trueLabel);
			writeFakeJumpIns(falseLabel);
		} else {
			// if ret<>0 then goto true
			// goto false
			translateExp(exp);
			String ret = getExpVar(exp);
			writeFakeJumpIfIns(JumpIfIns.NEQ, ret, "0", trueLabel);
			writeFakeJumpIns(falseLabel);
		}
	}*/

	private void translateBreakExp(InsBuffer ibuf, Exp exp, Frame frame) {
		ibuf.writeFakeJumpIns(breakToLabel);
	}

	private void translateNilExp(InsBuffer ibuf, NilExp exp, Frame frame) {
		// t0 := 0
		String t0 = newTempVar();
		ibuf.writeAssignIns(t0, "0");
		putExpVar(exp, t0);
	}

	private void translateExp(InsBuffer ibuf, Exp exp, Frame frame) {
		if (exp instanceof AssignExp)
			translateAssignExp(ibuf, (AssignExp)exp, frame);
		else if (exp instanceof LetExp)
			translateLetExp(ibuf, (LetExp)exp, frame);
		else if (exp instanceof CompExpList)
			translateCompExpList(ibuf, (CompExpList)exp, frame);
		else if (exp instanceof OpExp)
			translateOpExp(ibuf, (OpExp)exp, frame);
		else if (exp instanceof IdExp) 
			translateIdExp(ibuf, (IdExp)exp, frame);
		else if (exp instanceof IntExp)
			translateIntExp(ibuf, (IntExp)exp, frame);
		else if (exp instanceof StringExp)
			translateStringExp(ibuf, (StringExp)exp, frame);
		else if (exp instanceof ArrayInitExp)
			translateArrayInitExp(ibuf, (ArrayInitExp)exp, frame);
		else if (exp instanceof RecordInitExp)
			translateRecordInitExp(ibuf, (RecordInitExp)exp, frame);
		else if (exp instanceof ArrayAccessExp)
			translateArrayAccessExp(ibuf, (ArrayAccessExp)exp, frame);
		else if (exp instanceof RecordAccessExp)
			translateRecordAccessExp(ibuf, (RecordAccessExp)exp, frame);
		else if (exp instanceof FuncCallExp)
			translateFuncCallExp(ibuf, (FuncCallExp)exp, frame);
		else if (exp instanceof ForExp)
			translateForExp(ibuf, (ForExp)exp, frame);
		else if (exp instanceof WhileExp)
			translateWhileExp(ibuf, (WhileExp)exp, frame);
		else if (exp instanceof IfExp)
			translateIfExp(ibuf, (IfExp)exp, frame);
		else if (exp instanceof BreakExp)
			translateBreakExp(ibuf, (BreakExp)exp, frame);
		else if (exp instanceof NilExp)
			translateNilExp(ibuf, (NilExp)exp, frame);
		else
			assert(false);
	}

	public void dumpCode() {
		dumpCode(System.out);
	}

	public void dumpCode(PrintStream out) {
		for (Map.Entry<Function, InsBuffer> kv: funcIns.entrySet()) {
			Function func = kv.getKey();
			InsBuffer ibuf = kv.getValue();
			out.println("---- function " + userFuncName(func) + " (alias: "
					+ func.getName() + ")----");
			ibuf.dump(out);
		}
	}

	private String newTempVar() {
		return TEMP_VAR_PREFIX + (++maxTempVarId);
	}

	private String getExpVar(Exp exp) {
		return expVars.get(exp);
	}

	private void putExpVar(Exp exp, String var) {
		expVars.put(exp, var);
	}

	private Type getExpType(Exp exp) {
		Type type = expTypes.get(exp);
		if (type == null)
			return NoType.getInstance();
		else
			return type;
	}

	private int getStringId(String str) {
		int cnt = 0;
		for (String s: strs) {
			if (s.equals(str))
				return cnt;
			cnt++;
		}
		strs.add(str);
		return strs.size()-1;
	}

	private int getTypeSize(Type type) {
		if (type instanceof IntType)
			return arch.getIntSize();
		else if (type instanceof StringType)
			return arch.getIntSize();
		else if (type instanceof ArrayType)
			return arch.getIntSize();
		else if (type instanceof RecordType)
			return arch.getIntSize();
		else {
			assert(false);
			return -1;
		}
	}

	private String newLabel() {
		return "L" + (++maxLabelId);
	}

	/*private Variable getDeclVar(Decl decl) {
		return (Variable)declIds.get(decl);
	}

	private Function getDeclFunc(Decl decl) {
		return (Function)declIds.get(decl);
	}

	private Type getDeclType(Decl decl) {
		return (Type)declIds.get(decl);
	}*/

	private Identifier getExpId(IdExp exp) {
		return expIds.get(exp);
	}

	private void setBreakToLabel(String label) {
		breakToLabel = label;
	}

	private String getBreakToLabel(String label) {
		return breakToLabel;
	}

	private int maxLabelId = 0;
	private int maxTempVarId = 0;

	// private SyntaxTree syntaxTree;
	private Map<Exp, Type> expTypes;
	private Map<IdExp, Identifier> expIds;
	private Map<SyntaxTree, Identifier> declIds;
	private Function entryFunc;

	private Map<Exp, String> expVars = new HashMap<Exp, String>();
	private List<String> strs = new ArrayList<String>();
	private Map<Function, Integer> funcEntries = new HashMap<Function, Integer>();

	private Map<Function, InsBuffer> funcIns = new HashMap<Function, InsBuffer>();

	private String breakToLabel = null; // break to the line with label

	private Arch arch;
}

