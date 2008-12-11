package org.stepinto.carnivore.parser;

public class RecordInitExp extends Exp {
	public RecordInitExp(int lineNo, String id, FieldInitExpList initExps) {
		super(lineNo);
		this.id = id;
		this.initExps = initExps;
	}

	public String getId() { return id; }
	public FieldInitExpList getInitExps() { return initExps; }

	private String id;
	private FieldInitExpList initExps;
}
