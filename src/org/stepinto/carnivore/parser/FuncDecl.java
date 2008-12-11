package org.stepinto.carnivore.parser;

public class FuncDecl extends Decl {
	public FuncDecl(int lineNo, String id, TypeFieldList args, String retId, Exp body) {
		super(lineNo);
		this.id = id;
		this.args = args;
		this.body = body;
		this.retId = retId;
	}

	public String getId() { return id; }
	public TypeFieldList getArgs() { return args; }
	public Exp getBody() { return body; }
	public String getRetId() { return retId; }

	private String id;
	private TypeFieldList args;
	private Exp body;
	private String retId;
}
