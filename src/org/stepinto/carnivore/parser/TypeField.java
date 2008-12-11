package org.stepinto.carnivore.parser;

public class TypeField extends SyntaxTree {
	public TypeField(int lineNo, String varId, String typeId) {
		super(lineNo);
		this.varId = varId;
		this.typeId = typeId;
	}

	public String getVarId() { return varId; }
	public String getTypeId() { return typeId; }

	private String varId, typeId;
}
