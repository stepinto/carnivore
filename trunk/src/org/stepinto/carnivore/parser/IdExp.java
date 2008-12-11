package org.stepinto.carnivore.parser;

public class IdExp extends Exp {
	public IdExp(int lineNo, String id) {
		super(lineNo);
		this.id = id;
	}

	public String getId() { return id; }

	private String id;
}
