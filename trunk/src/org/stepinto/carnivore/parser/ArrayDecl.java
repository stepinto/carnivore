package org.stepinto.carnivore.parser;

public class ArrayDecl extends TypeDecl {
	public ArrayDecl(int lineNo, String id, String elemTypeId) {
		super(lineNo, id);
		this.elemTypeId = elemTypeId;
	}

	public String getElemTypeId() { return elemTypeId; }

	private String elemTypeId;
}

