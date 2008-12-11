package org.stepinto.carnivore.parser;
import java.util.*;

public class DeclList extends SyntaxTree {
	public DeclList(int lineNo) {
		super(lineNo);
	}

	public DeclList(int lineNo, Decl decl) {
		super(lineNo);
		list.add(decl);
	}

	public DeclList(int lineNo, DeclList src, Decl d) {
		super(lineNo);
		list.addAll(src.list);
		list.add(d);
	}

	private List<Decl> list = new ArrayList<Decl>();
	public List<Decl> getDecls() {
		return list;
	}
}
