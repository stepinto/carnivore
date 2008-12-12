package org.stepinto.carnivore.ir;

public class ParamIns extends Ins {
	public ParamIns(int lineNo, String param) {
		super(lineNo);
		assert(param != null);
		this.param = param;
	}

	public String toString() {
		return getLineNo() + "\tparam " + param;
	}

	public String getParam() {
		return param;
	}

	private String param;
}

