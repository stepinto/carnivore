package org.stepinto.carnivore.parser;

public class VarDecl extends Decl {
	public VarDecl(int lineNo, String id, String typeId, Exp initExp) {
		super(lineNo);
		this.id = id;
		this.typeId = typeId;
		this.initExp = initExp;
	}

	public String getVarId() { return id; }
	public String getTypeId() { return typeId; }
	public Exp getInitExp() { return initExp; }

	private String id, typeId;
	private Exp initExp;
}

