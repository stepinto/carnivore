package org.stepinto.carnivore.ir;

public class ReturnIns extends Ins {
	public ReturnIns(int lineNo) {
		super(lineNo);
		this.retValue = null;
	}

	public ReturnIns(int lineNo, String retValue) {
		super(lineNo);
		this.retValue = retValue;
	}

	public String toString() {
		if (retValue == null)
			return getLineNo() + "\treturn";
		else
			return getLineNo() + "\treturn " + retValue;
	}

	private String retValue;
}

