package org.stepinto.carnivore.parser;
import java.util.*;

public class FieldInitExpList extends SyntaxTree {
	public FieldInitExpList(int lineNo) {
		super(lineNo);
	}

	public FieldInitExpList(int lineNo, FieldInitExp exp) {
		super(lineNo);
		list.add(exp);
	}

	public FieldInitExpList(int lineNo, FieldInitExpList expList, FieldInitExp exp) {
		super(lineNo);
		list.addAll(expList.list);
		list.add(exp);
	}

	public List<FieldInitExp> getExps() { return list; }

	List<FieldInitExp> list = new ArrayList<FieldInitExp>();
}
