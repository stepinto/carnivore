package org.stepinto.carnivore.parser;

public class IntExp extends Exp {
	public IntExp(int lineNo, int value) {
		super(lineNo);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	private int value;
}
