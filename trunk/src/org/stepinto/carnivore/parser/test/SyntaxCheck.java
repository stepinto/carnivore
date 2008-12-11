package org.stepinto.carnivore.parser.test;

import java.io.*;
import java_cup.runtime.*;
import org.stepinto.carnivore.common.*;
import org.stepinto.carnivore.parser.*;

public class SyntaxCheck {
	public static void main(String []args) throws Exception {
		ErrorManager errorMgr = new ErrorManager();
		Scanner scanner = new Yylex(System.in, errorMgr);
		Parser parser = new Parser(scanner, errorMgr);

		try {
			parser.parse();
		} catch (Exception ex) {
			System.err.println("Unexpected error.");
			System.exit(1);
		}

		if (errorMgr.hasErrors()) {
			errorMgr.printAll();
			System.exit(1);
		}
	}
};
