package org.stepinto.carnivore.ir;

// var local t0;
// var param t0;
public class VarDeclIns extends Ins {
	public VarDeclIns(int lineNo, String name, boolean local) {
		super(lineNo);
		this.name = name;
		this.local = local;
	}

	public String toString() {
		return getLineNo() + "\tvar " + (local ? "local " : "param ") + name;
	}

	private boolean local;
	private String name;
}

