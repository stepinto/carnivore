package org.stepinto.carnivore.sematics;

public class StringType extends Type {
	private StringType() { super("string"); }

	public boolean equals(Object obj) {
		return (obj instanceof StringType);
	}

	public static StringType getInstance() {
		return instance;
	}

	private static StringType instance = new StringType();
}

