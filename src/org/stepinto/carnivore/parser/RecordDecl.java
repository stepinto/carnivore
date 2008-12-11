package org.stepinto.carnivore.parser;

public class RecordDecl extends TypeDecl {
	public RecordDecl(int lineNo, String id, TypeFieldList fields) {
		super(lineNo, id);
		this.fields = fields;
	}

	public TypeFieldList getFields() { return fields; }

	private TypeFieldList fields;
}
