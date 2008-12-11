package org.stepinto.carnivore.parser;

public class TypeDecl extends Decl {
	public TypeDecl(int lineNo, String id) {
		super(lineNo);
		this.id = id;
	}

	public String getId() { return id; }
	private String id;
}

