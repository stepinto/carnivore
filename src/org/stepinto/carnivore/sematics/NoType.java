package org.stepinto.carnivore.sematics;

public class NoType extends Type {
	private NoType() { super("no type"); }
	public static NoType getInstance() { return instance; }
	private static NoType instance = new NoType();
}

