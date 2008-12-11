package org.stepinto.carnivore.parser;

public class FuncCallExp extends Exp {
	public FuncCallExp(int lineNo, Exp id, FuncCallArgList args) {
		super(lineNo);
		this.id = id;
		this.args = args;
	}

	public Exp getId() { return id; }
	public FuncCallArgList getArgs(){ return args; }

	private Exp id;
	private FuncCallArgList args;
}
