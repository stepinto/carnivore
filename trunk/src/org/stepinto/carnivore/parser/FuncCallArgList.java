package org.stepinto.carnivore.parser;
import java.util.*;

public class FuncCallArgList extends SyntaxTree {
	public FuncCallArgList(int lineNo) {
		super(lineNo);
	}

	public FuncCallArgList(int lineNo, Exp arg) {
		super(lineNo);
		list.add(arg);
	}

	public FuncCallArgList(int lineNo, FuncCallArgList src, Exp exp) {
		super(lineNo);
		list.addAll(src.list);
		list.add(exp);
	}

	public List<Exp> getArgs() { return list; }
	private List<Exp> list = new ArrayList<Exp>();
}
