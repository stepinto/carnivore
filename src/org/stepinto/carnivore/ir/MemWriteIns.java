package org.stepinto.carnivore.ir;

// base[index] := right;
public class MemWriteIns extends Ins {
	public MemWriteIns(int lineNo, String base, String index, String right) {
		super(lineNo);
		this.base = base;
		this.index = index;
		this.right = right;
	}

	public String toString() {
		return getLineNo() + "\t" + base + "[" + index + "] := " + right;
	}

	private String base, index, right;
}

