package org.stepinto.carnivore.ir;

// left = base[index];
public class MemReadIns extends Ins {
	public MemReadIns(int lineNo, String left, String base, String index) {
		super(lineNo);
		this.left = left;
		this.base = base;
		this.index = index;
	}

	public String toString() {
		return getLineNo() + "\t" + left + " := " + base + "[" + index + "]";
	}

	private String left, base, index;
}

