package org.stepinto.carnivore.optimize.controlflow.test;

import java.util.*;
import org.stepinto.carnivore.common.*;
import org.stepinto.carnivore.parser.*;
import org.stepinto.carnivore.sematics.*;
import org.stepinto.carnivore.ir.*;
import org.stepinto.carnivore.arch.*;
import org.stepinto.carnivore.arch.x86.*;
import org.stepinto.carnivore.optimize.controlflow.*;

public class ExtractControlFlow {
	public static void main(String []args) {
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
		
		Translator tl = new Translator(tc.getExpTypeTable(),
				tc.getDeclIdTable(), tc.getExpIdTable(), tc.getEntryFunc(),
				intelArch);
		tl.translate();

		// extract control-flow diagram
		for (Map.Entry<Function, InsBuffer> entry : tl.getFuncIns().entrySet()) {
			Function func = entry.getKey();
			InsBuffer ibuf = entry.getValue();
			List<Block> blocks = new Analyzer(ibuf).findBlocks();

			for (Block block : blocks) {
				System.out.println("------");
				for (Ins ins : block.getIns())
					System.out.println(ins);
			}
		}

		System.exit(0);
	}
}

