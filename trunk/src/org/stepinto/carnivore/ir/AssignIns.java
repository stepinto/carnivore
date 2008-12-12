package org.stepinto.carnivore.ir;

public class AssignIns extends Ins {
	public AssignIns(int lineNo, String left, String right) {
		super(lineNo);
		assert(left != null);
		assert(right != null);
		this.left = left;
		this.right = right;
	}

	public String toString() {
		return getLineNo() + "\t" + left + " := " + right; 
	}

	public String getLeft() {
		return left;
	}

	public String getRight() {
		return right;
	}

	private String left, right;
}

