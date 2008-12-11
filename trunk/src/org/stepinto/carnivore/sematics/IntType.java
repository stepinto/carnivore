package org.stepinto.carnivore.sematics;

public class IntType extends Type {
	private IntType() { super("int"); }
	
	public boolean equals(Object obj) {
		return (obj instanceof IntType);
	}

	public static IntType getInstance() {
		return instance;
	}

	private static IntType instance = new IntType();
}

