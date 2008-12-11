package org.stepinto.carnivore.parser;
import java.util.*;

public class CompExpList extends Exp {
	public CompExpList(int lineNo, Exp exp) {
		super(lineNo);
		list.add(exp);
	}

	public CompExpList(int lineNo, CompExpList srcList, Exp exp) {
		super(lineNo);
		list.addAll(srcList.list);
		list.add(exp);
	}

	public CompExpList(int lineNo, CompExpList list) {
		super(lineNo);
		this.list.addAll(list.list);
	}

	public CompExpList(int lineNo) {
		super(lineNo);
	}

	private List<Exp> list = new ArrayList<Exp>();
	
	public List<Exp> getExps() {
		return list;
	}
}

