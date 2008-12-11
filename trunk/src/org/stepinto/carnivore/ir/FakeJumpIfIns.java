package org.stepinto.carnivore.ir;

public class FakeJumpIfIns extends FakeIns {
	public FakeJumpIfIns(int lineNo, int relOp, String operand1, String operand2, String label) {
		super(lineNo);
		this.relOp = relOp;
		this.operand1 = operand1;
		this.operand2 = operand2;
		this.label = label;
	}

	private String label, operand1, operand2;
	private int relOp;
	public String getTarget() { return label; }
	public String getOperand1() { return operand1; }
	public String getOperand2() { return operand2; }
	public int getRelOp() { return relOp; }
}


