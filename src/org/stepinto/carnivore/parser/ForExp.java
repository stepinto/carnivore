package org.stepinto.carnivore.parser;

public class ForExp extends Exp {
	public ForExp(int lineNo, String varId, Exp from, Exp to, Exp body) {
		super(lineNo);
		this.varId = varId;
		this.from = from;
		this.to = to;
		this.body = body;
	}

	public String getVarId() { return varId; }
	public Exp getFromExp() { return from; }
	public Exp getToExp() { return to; }
	public Exp getBody() { return body; }
	
	private String varId;
	private Exp from, to, body;
}
