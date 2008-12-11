package org.stepinto.carnivore.parser;

public class IfExp extends Exp {
	public IfExp(int lineNo, Exp condi, Exp thenBody, Exp elseBody) {
		super(lineNo);
		this.condi = condi;
		this.thenBody = thenBody;
		this.elseBody = elseBody;
	}

	public Exp getCondiExp() { return condi; }
	public Exp getThenBody() { return thenBody; }
	public Exp getElseBody() { return elseBody; }

	private Exp condi, thenBody, elseBody;
}
