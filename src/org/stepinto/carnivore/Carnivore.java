package org.stepinto.carnivore;

import java.io.*;
import java_cup.runtime.*;
import org.stepinto.carnivore.common.*;
import org.stepinto.carnivore.parser.*;

public class Carnivore {
	public static void main(String []args) throws Exception {
		ErrorManager errorMgr = new ErrorManager();
		Scanner scanner = new Yylex(System.in, errorMgr);
		Parser parser = new Parser(scanner, errorMgr);
		parser.parse();
		errorMgr.printAll();
	}
};
