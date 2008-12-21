package org.stepinto.carnivore.common;

import java.io.*;
import java.util.*;

public class ErrorManager {
	public void printAll() {
		printAll(System.err);
	}

	public void printAll(PrintStream out) {
		for (ErrorMessage e : errorMsg)
			out.println(e);
	}

	public void report(int line, String msg) {
		errorMsg.add(new ErrorMessage(line, msg));
	}

	public boolean hasErrors() {
		return !errorMsg.isEmpty();
	}
	
	private List<ErrorMessage> errorMsg = new ArrayList<ErrorMessage>();
};

