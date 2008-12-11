package org.stepinto.carnivore.parser;

public class RecordAccessExp extends Exp {
	public RecordAccessExp(int lineNo, Exp id, String fieldId) {
		super(lineNo);
		this.id = id;
		this.fieldId = fieldId;
	}

	public Exp getId() { return id; }
	public String getFieldId() { return fieldId; }

	private Exp id;
	private String fieldId;
}

