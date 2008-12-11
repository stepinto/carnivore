package org.stepinto.carnivore.sematics.test;

import org.stepinto.carnivore.common.*;
import org.stepinto.carnivore.parser.*;
import org.stepinto.carnivore.sematics.*;
import org.stepinto.carnivore.arch.x86.*;

public class CheckType {
	public static void main(String []args) {
		boolean debug = (args.length > 0 && args[0].equals("--debug"));
		ErrorManager err = new ErrorManager();
		Yylex scn = new Yylex(System.in, err);
		Parser parser = new Parser(scn, err);

		try {
			parser.parse();
		} catch (Exception ex) {
			System.err.println("Unexpected error in parsing.");
			System.exit(1);
		}
		if (err.hasErrors()) {
			err.printAll();
			System.exit(1);
		}

		TypeChecker tc = new TypeChecker(parser.getSyntaxTree(), err, IntelArch.getInstance(), debug);
		tc.check();
		if (err.hasErrors()) {
			err.printAll();
			System.exit(1);
		}

		System.exit(0);
	}
}

