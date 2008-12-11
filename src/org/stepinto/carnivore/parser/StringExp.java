package org.stepinto.carnivore.parser;

public class StringExp extends Exp {
	private String value;

	public StringExp(int lineNo, String value) {
		super(lineNo);
		this.value = value;
	}

	public String getValue() { return value; }
}
