package org.stepinto.carnivore.ir;

public class FuncDeclIns extends Ins {
	public FuncDeclIns(int lineNo, String name) {
		super(lineNo);
		this.name = name;
	}

	public String toString() {
		return getLineNo() + "\tfunc" + name;
	}

	public String getName() {
		return name;
	}

	private String name;
}

