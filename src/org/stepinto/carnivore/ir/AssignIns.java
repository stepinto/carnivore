package org.stepinto.carnivore.ir;

public class AssignIns extends Ins {
	public AssignIns(int lineNo, String left, String right) {
		super(lineNo);
		this.left = left;
		this.right = right;
	}

	public String toString() {
		return getLineNo() + "\t" + left + " := " + right; 
	}

	private String left, right;
}

