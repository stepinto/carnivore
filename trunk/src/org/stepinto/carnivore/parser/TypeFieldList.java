package org.stepinto.carnivore.parser;
import java.util.*;

public class TypeFieldList extends SyntaxTree {
	public TypeFieldList(int lineNo) {
		super(lineNo);
	}

	public TypeFieldList(int lineNo, TypeField tf) {
		super(lineNo);
		list.add(tf);
	}

	public TypeFieldList(int lineNo, TypeFieldList src, TypeField tf) {
		super(lineNo);
		list.addAll(src.list);
		list.add(tf);
	}

	public List<TypeField> getFields() { return list; }
	private List<TypeField> list = new ArrayList<TypeField>();
}
