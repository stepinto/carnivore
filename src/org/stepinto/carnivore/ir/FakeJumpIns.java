package org.stepinto.carnivore.ir;

public class FakeJumpIns extends FakeIns {
	public FakeJumpIns(int lineNo, String label) { super(lineNo); this.label = label; }
	private String label;
	public String getTarget() { return label; }
}


