package org.stepinto.carnivore.parser;

public class TypeAliasDecl extends TypeDecl {
	public TypeAliasDecl(int lineNo, String id, String org) {
		super(lineNo, id);
		this.org = org;
	}

	public String getAlias() { return getId();  }
	public String getRealName() { return org; }

	String org;
}
