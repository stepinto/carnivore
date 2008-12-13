package org.stepinto.carnivore.sematics;
import java.util.*;

public class RuntimeFunctions {
	private static List<Variable> makeArgList() {
		return new ArrayList<Variable>();
	}

	private static List<Variable> makeArgList(Type t1) {
		List<Variable> list = new ArrayList<Variable>();
		list.add(new Variable("arg1", t1, null, false, null));
		return list;
	}

	private static List<Variable> makeArgList(Type t1, Type t2) {
		List<Variable> list = new ArrayList<Variable>();
		list.add(new Variable("arg1", t1, null, false, null));
		list.add(new Variable("arg1", t2, null, false, null));
		return list;
	}

	private static List<Variable> makeArgList(Type t1, Type t2, Type t3) {
		List<Variable> list = new ArrayList<Variable>();
		list.add(new Variable("arg1", t1, null, false, null));
		list.add(new Variable("arg2", t2, null, false, null));
		list.add(new Variable("arg3", t3, null, false, null));
		return list;
	}

	private List<Function> list;
	private static RuntimeFunctions instance = new RuntimeFunctions();

	// private constructor
	// add pre-defined runtime functions into the list
	private RuntimeFunctions() {
		list = new ArrayList<Function>();
		IntType intType = IntType.getInstance();
		StringType stringType = StringType.getInstance();

		// print
		Function print = new Function("print", makeArgList(stringType), null, null, null);
		list.add(print);

		// printi
		Function printi = new Function("printi", makeArgList(intType), null, null, null);
		list.add(printi);

		// flush
		Function flush = new Function("flush", makeArgList(), null, null, null);
		list.add(flush);
		
		// getchar
		Function getchar = new Function("getchar", makeArgList(), stringType, null, null);
		list.add(getchar);

		// geti
		Function geti = new Function("geti", makeArgList(), intType, null, null);
		list.add(geti);

		// ord
		Function ord = new Function("ord", makeArgList(stringType), intType, null, null);
		list.add(ord);

		// chr
		Function chr = new Function("chr", makeArgList(intType), stringType, null, null);
		list.add(chr);
		
		// size
		Function size = new Function("size", makeArgList(stringType), intType, null, null);
		list.add(size);

		// substring
		Function substring = new Function("substring", makeArgList(stringType, intType, intType),
				stringType, null, null);
		list.add(substring);

		// concat
		Function concat = new Function("concat", makeArgList(stringType, stringType), stringType,
				null, null);
		list.add(concat);

		// not
		Function not = new Function("not", makeArgList(intType), intType, null, null);
		list.add(not);
		
		// exit
		Function exit = new Function("exit", makeArgList(intType), null, null, null);
		list.add(exit);
	}

	// return a list of runtime functions.
	// each function in the list is unique globally. i.e. when getList() is called
	// next time, it returns the same list and function objects
	public static List<Function> getList() {
		return instance.list;
	}

	public static boolean isRuntimeFunc(Function func) {
		for (Function f: getList())
			if (f.equals(func))
				return true;
		return false;
	}
}

