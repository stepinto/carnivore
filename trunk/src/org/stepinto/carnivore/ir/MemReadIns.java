package org.stepinto.carnivore.ir;

// left = base[index];
public class MemReadIns extends Ins {
	public MemReadIns(int lineNo, String left, String base, String index) {
		super(lineNo);
		assert(left != null);
		assert(base != null);
		assert(index != null);
		this.left = left;
		this.base = base;
		this.index = index;
	}

	public String toString() {
		return getLineNo() + "\t" + left + " := " + base + "[" + index + "]";
	}

	public String getLeft() {
		return left;
	}

	public String getBase() {
		return base;
	}

	public String getIndex() {
		return index;
	}

	private String left, base, index;
}

