package org.stepinto.carnivore.parser;

public class LetExp extends Exp {
	public LetExp(int lineNo, DeclList decls, Exp body) {
		super(lineNo);
		this.decls = decls;
		this.body = body;
	}

	public DeclList getDecls() { return decls; }
	public Exp getBody() { return body; }

	private DeclList decls;
	private Exp body;
}
