package org.stepinto.carnivore.common;

class ErrorMessage {
	public ErrorMessage(int lineNo, String message) {
		this.lineNo = lineNo;
		this.message = message;
	}
	public int getLineNo() { return lineNo; }
	public String getMessage() { return message; }
	public String toString() { return lineNo + ": " + message; }

	private int lineNo;
	private String message;
};


