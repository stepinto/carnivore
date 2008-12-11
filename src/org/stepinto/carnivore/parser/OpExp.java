package org.stepinto.carnivore.parser;

public class OpExp extends Exp {
	public static final int PLUS = 0;
	public static final int MINUS = 1;
	public static final int TIMES = 2;
	public static final int DIVIDE = 3;
	public static final int EQ = 4;
	public static final int LT = 5;
	public static final int LE = 6;
	public static final int GT = 7;
	public static final int GE = 8;
	public static final int NEQ = 9;

	public OpExp(int lineNo, Exp left, int op, Exp right) {
		super(lineNo);
		this.left = left;
		this.right = right;
		this.op = op;
	}

	public Exp getLeft() { return left; }
	public Exp getRight() { return right; }
	public int getOp() { return op; }

	private Exp left, right;
	private int op;
}
