package org.stepinto.carnivore.sematics;

public class TypeAlias extends Type {
	public TypeAlias(String alias, Type actualType) {
		super(alias);
		this.actualType = actualType;
	}

	public Type getActualType() { return actualType; }
	private Type actualType;
}

