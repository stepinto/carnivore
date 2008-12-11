package org.stepinto.carnivore.ir;

public class NopIns extends Ins {
	public NopIns(int lineNo) { 
		super(lineNo);
	}

	public String toString() {
		return getLineNo() + "\tnop";
	}
}

