package org.stepinto.carnivore.ir;

public class JumpIns extends Ins {
	public JumpIns(int lineNo, int target) {
		super(lineNo);
		this.target = target;
	}

	public String toString() {
		return getLineNo() + "\tjump " + target;
	}

	private int target;
}

