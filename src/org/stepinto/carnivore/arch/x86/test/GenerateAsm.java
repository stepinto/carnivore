package org.stepinto.carnivore.arch.x86.test;

import java.util.*;
import java.io.*;
import org.stepinto.carnivore.common.*;
import org.stepinto.carnivore.parser.*;
import org.stepinto.carnivore.sematics.*;
import org.stepinto.carnivore.ir.*;
import org.stepinto.carnivore.arch.*;
import org.stepinto.carnivore.arch.x86.*;

public class GenerateAsm {
	public static void main(String args[]) {
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

		Arch intelArch = IntelArch.getInstance();
		TypeChecker tc = new TypeChecker(parser.getSyntaxTree(), err, intelArch);
		tc.check();
		if (err.hasErrors()) {
			err.printAll();
			System.exit(1);
		}
		
		Translator tl = new Translator(/*parser.getSyntaxTree(), */tc.getExpTypeTable(),
				tc.getDeclIdTable(), tc.getExpIdTable(), tc.getEntryFunc(),
				intelArch);
		tl.translate();

		intelArch.newGenerator(tl.getFuncIns(), tl.getStringTable(), System.out).generate();
		System.exit(0);

	}
}

