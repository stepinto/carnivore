package org.stepinto.carnivore.ir;

public class Ins {
	public Ins(int lineNo) {
		this.lineNo = lineNo;
	}

	public int getLineNo() { return lineNo; }

	// called by InsBuffer.writeIns()
	void setLineNo(int lineNo) { this.lineNo = lineNo; }

	private int lineNo;
}

