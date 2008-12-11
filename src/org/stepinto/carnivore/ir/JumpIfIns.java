package org.stepinto.carnivore.ir;

// if a < b then jump xxx;
public class JumpIfIns extends Ins {
	public static final int LT = 1;
	public static final int LE = 2;
	public static final int EQ = 3;
	public static final int NEQ = 4;
	public static final int GT = 5;
	public static final int GE = 6;

	public JumpIfIns(int lineNo, int relOp, String operand1, String operand2, int target) {
		super(lineNo);
		this.relOp = relOp;
		this.operand1 = operand1;
		this.operand2 = operand2;
		this.target = target;
	}

	private String getOpString() {
		switch (relOp) {
			case LT: return "<";
			case LE: return "<=";
			case EQ: return "=";
			case NEQ: return "<>";
			case GT: return ">";
			case GE: return ">=";
			default: assert(false);
		}
		return "!! unknown !!";
	}

	public String toString() {
		return getLineNo() + "\tif " + operand1 + " " + getOpString() + " "
	       		+ operand2 + " jump " + target;
	}

	private int relOp;
	private int target;
	private String operand1, operand2;
}

