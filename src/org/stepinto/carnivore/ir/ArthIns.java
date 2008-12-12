package org.stepinto.carnivore.ir;

public class ArthIns extends Ins {
	public static final int PLUS = 1;
	public static final int MINUS = 2;
	public static final int TIMES = 3;
	public static final int DIVIDE = 4;

	public ArthIns(int lineNo, int op, String result, String operand1, String operand2) {
		super(lineNo);
		assert(result != null);
		assert(operand1 != null);
		assert(operand2 != null);
		this.result = result;
		this.operand1 = operand1;
		this.operand2 = operand2;
		this.op = op;
	}

	private char getOpChar() {
		switch (op) {
			case PLUS: return '+';
			case MINUS: return '-';
			case TIMES: return '*';
			case DIVIDE: return '/';
			default: assert(false); return ' ';
		}
	}

	public String toString() {
		return getLineNo() + "\t" + result + " := " + operand1 + " " + getOpChar()
			+ " " + operand2;
	}

	public String getResult() {
		return result;
	}

	public String getLeft() { 
		return operand1;
	}

	public String getRight() {
		return operand2;
	}

	public int getOp() {
		return op;
	}

	private int op;
	private String result, operand1, operand2;
}

