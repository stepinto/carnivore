package org.stepinto.carnivore.parser;

public class ArrayInitExp extends Exp {
	public ArrayInitExp(int lineNo, String typeId, Exp size, Exp initValue) {
		super(lineNo);
		this.typeId = typeId;
		this.initValue = initValue;
		this.size = size;
	}

	public String getTypeId() { return typeId; }
	public Exp getSizeExp() { return size; }
	public Exp getElemInitExp() { return initValue; }

	private String typeId;
	private Exp size;
	private Exp initValue;
}

