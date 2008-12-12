package org.stepinto.carnivore.ir;

// base[index] := right;
public class MemWriteIns extends Ins {
	public MemWriteIns(int lineNo, String base, String index, String right) {
		super(lineNo);
		assert(base != null);
		assert(index != null);
		assert(right != null);
		this.base = base;
		this.index = index;
		this.right = right;
	}

	public String toString() {
		return getLineNo() + "\t" + base + "[" + index + "] := " + right;
	}

	public String getBase() {
		return base;
	}

	public String getIndex() {
		return index;
	}

	public String getRight() {
		return right;
	}

	private String base, index, right;
}

