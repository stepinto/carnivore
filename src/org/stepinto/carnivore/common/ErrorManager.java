package org.stepinto.carnivore.common;

import java.util.*;

public class ErrorManager {
	public void printAll() {
		for (ErrorMessage e : errorMsg)
			System.err.println(e);
	}

	public void report(int line, String msg) {
		errorMsg.add(new ErrorMessage(line, msg));
	}

	public boolean hasErrors() {
		return !errorMsg.isEmpty();
	}
	
	private List<ErrorMessage> errorMsg = new ArrayList<ErrorMessage>();
};

