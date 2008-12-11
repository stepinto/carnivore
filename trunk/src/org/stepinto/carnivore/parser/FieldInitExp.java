package org.stepinto.carnivore.parser;

public class FieldInitExp extends SyntaxTree {
	public FieldInitExp(int lineNo, String id, Exp value) {
		super(lineNo);
		this.id = id;
		this.value = value;
	}

	public String getId() { return id; }
	public Exp getExp() { return value; }

	private String id;
	private Exp value;
}

