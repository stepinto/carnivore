package org.stepinto.carnivore.parser;

public class WhileExp extends Exp {
	private Exp condi, body;

	public WhileExp(int lineNo, Exp condi, Exp body) {
		super(lineNo);
		this.condi = condi;
		this.body = body;
	}

	public Exp getCondiExp() { return condi; }
	public Exp getBody() { return body; }
}
