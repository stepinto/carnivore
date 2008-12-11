package org.stepinto.carnivore.sematics;

public class ArrayType extends Type {
	public ArrayType(String name, Type elemType) {
		super(name);
		this.elemType = elemType;
	}

	public Type getElemType() { return elemType; }

	private Type elemType;
}

