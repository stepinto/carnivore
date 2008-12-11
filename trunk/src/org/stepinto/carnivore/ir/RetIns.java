package org.stepinto.carnivore.ir;

public class RetIns extends Ins {
	public RetIns(int lineNo) {
		super(lineNo);
	}

	public RetIns(int lineNo, String var) {
		super(lineNo);
		this.var = var;
	}

	public String toString() {
		return getLineNo() + "\tret" + (var == null ? "" : " " + var);
	}

	private String var;
}

