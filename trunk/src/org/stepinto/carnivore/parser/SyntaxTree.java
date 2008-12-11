package org.stepinto.carnivore.parser;

public class SyntaxTree {
	SyntaxTree(int lineNo) {
		this.lineNo = lineNo;
	}

	private int lineNo;

	public int getLineNo() {
		return lineNo;
	}
}



