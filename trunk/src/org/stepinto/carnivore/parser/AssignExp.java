package org.stepinto.carnivore.parser;

public class AssignExp extends Exp {
	public AssignExp(int lineNo, Exp left, Exp right) {
		super(lineNo);
		this.left = left;
		this.right = right;
	}

	private Exp left, right;
	public Exp getLeft() { return left; }
	public Exp getRight() { return right; }
}
