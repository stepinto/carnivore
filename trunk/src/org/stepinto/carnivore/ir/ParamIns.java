package org.stepinto.carnivore.ir;

public class ParamIns extends Ins {
	public ParamIns(int lineNo, String param) {
		super(lineNo);
		this.param = param;
	}

	public String toString() {
		return getLineNo() + "\tparam " + param;
	}

	private String param;
}

