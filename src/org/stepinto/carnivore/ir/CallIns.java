package org.stepinto.carnivore.ir;

// call 50
// t0 := call 50
public class CallIns extends Ins {
	public CallIns(int lineNo, String left, String target) {
		super(lineNo);
		this.left = left;
		this.target = target;
	}

	public CallIns(int lineNo, String target) {
		super(lineNo);
		this.left = null;
		this.target = target;
	}

	public String toString() {
		if (left == null)
			return getLineNo() + "\tcall " + target;
		else
			return getLineNo() + "\t" + left + " := call " + target;
	}

	private String target; 
	private String left;
}

