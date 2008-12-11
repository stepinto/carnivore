package org.stepinto.carnivore.ir;

public class FakeIns extends Ins {
	public FakeIns(int lineNo) {
		super(lineNo);
	}

	public String toString() {
		return "!! fake-ins !!";
	}
}

