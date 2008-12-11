package org.stepinto.carnivore.sematics;

public class SematicException extends Throwable {
	public SematicException(int lineNo, String msg) { 
		this.lineNo = lineNo;
		this.msg = msg;
	}

	public int getLineNo() { return lineNo; }
	public String getMessage() { return msg; }

	private int lineNo;
	private String msg;
}

