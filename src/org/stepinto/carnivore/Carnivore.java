package org.stepinto.carnivore;

import java.io.*;
import java.util.*;
import org.stepinto.carnivore.arch.*;
import org.stepinto.carnivore.arch.x86.*;
import org.stepinto.carnivore.common.*;
import org.stepinto.carnivore.ir.*;
import org.stepinto.carnivore.parser.*;
import org.stepinto.carnivore.sematics.*;
import org.stepinto.carnivore.optimize.*;

public class Carnivore {
	public Carnivore(Config config) {
		this.config = config;
	}

	private List<Optimizer> getOptimizers() {
		List<Optimizer> list = new ArrayList<Optimizer>();
		list.add(new NopEraser());
		return list;
	}
	
	public void compile(PrintStream out) throws CompileException {
		ErrorManager errMgr = new ErrorManager();
		Yylex scn = null;
		try {
			scn = new Yylex(new FileInputStream(config.getSource()), errMgr);
		} catch (FileNotFoundException ex) {
			throw new CompileException("Source code cannot be read.");
		}
		Parser parser = new Parser(scn, errMgr);

		// print input/output messages
		if (config.isVerbose())
			config.dump(out);

		// parse
		printlnVerbosed(out, "Parsing...");
		try {
			parser.parse();
		} catch (Exception ex) {
			throw newCompileException(errMgr);
		}

		// type-check
		Arch arch = IntelArch.getInstance();
		TypeChecker tc = new TypeChecker(parser.getSyntaxTree(), errMgr, arch);
		printlnVerbosed(out, "Type-checking...");
		tc.check();
		if (errMgr.hasErrors())
			throw newCompileException(errMgr);

		// translate
		Translator tl = new Translator(tc.getExpTypeTable(), tc.getDeclIdTable(), tc.getExpIdTable(),
						tc.getEntryFunc(), arch);
		printlnVerbosed(out, "Translating...");
		tl.translate();

		// optimize
		if (config.isOptimized()) {
			printlnVerbosed(out, "Optimizing...");
			for (Optimizer opt : getOptimizers()) {
				printlnVerbosed(out, "\tRunning " + opt.getClass().getSimpleName() + "...");
				for (Map.Entry<Function, InsBuffer> funcIns : tl.getFuncIns().entrySet()) {
					InsBuffer oldIbuf = funcIns.getValue();
					InsBuffer newIbuf = opt.optimize(oldIbuf);
					funcIns.setValue(newIbuf);
				}
			}
		}
		if (config.getTargetCode() == Config.TC_IR) {
			try {
				tl.dumpCode(new PrintStream(config.getTarget()));
			} catch (FileNotFoundException ex) {
				throw new CompileException("Unexpected failure during translation.");
			}
			return;
		}


		// generate asm-code
		File asmFile;
		printlnVerbosed(out, "Generating assembly code...");
		if (config.getTargetCode() == Config.TC_ASM)
			asmFile = config.getTarget();
		else {
			try {
				asmFile = File.createTempFile("carni", ".asm");
			} catch (IOException ex) {
				throw new CompileException("Unexpected failure during generating assembly code.");
			}
			asmFile.deleteOnExit();
		}
		try {
			arch.newGenerator(tl.getFuncIns(), tl.getStringTable(), tc.getEntryFunc(),
						new PrintStream(asmFile)).generate();
		} catch (FileNotFoundException ex) {
			throw new CompileException("Unexpected failure during generating assembly code.");
		}
		if (config.getTargetCode() == Config.TC_ASM)
			return;

		// run nasm to produce .o
		File objFile;
		if (config.getTargetCode() == Config.TC_OBJ)
			objFile = config.getTarget();
		else {
			try {
				objFile = File.createTempFile("carni", ".o");
			} catch (IOException ex) {
				throw new CompileException("Unexpected failure during generating object code.");
			}
			objFile.deleteOnExit();
		}
		printlnVerbosed(out, "Assembling...");
		try {
			new AssemblerProxy(asmFile, objFile, config.isDebug()).assemble();
		} catch (AssemblerException ex) {
			throw new CompileException("Unexpected failure during assembling.");
		}
		if (config.getTargetCode() == Config.TC_OBJ)
			return;

		// run linker to link .o files into a executable
		assert(config.getTargetCode() == Config.TC_EXE);
		printlnVerbosed(out, "Linking...");
		try {
			List<File> objFiles = new ArrayList<File>();
			objFiles.add(objFile);
			if (!getRuntimeFile().exists())
				throw new CompileException("runtime.a cannot be not found. Linking stopped.");
			objFiles.add(getRuntimeFile());
			new LinkerProxy(objFiles, config.getTarget(), config.isDebug()).link();
		} catch (LinkerException ex) {
			throw new CompileException("Unexpected failure during linking.");
		}

		return;
	}

	public File getRuntimeFile() {
		return new File("runtime.a");
	}

	public CompileException newCompileException(ErrorManager errMgr) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		return new CompileException(baos.toString());
	}

	public void printlnVerbosed(PrintStream out, String msg) {
		if (config.isVerbose())
			out.println(msg);
	}

	public static void printHelp(PrintStream out) {
		out.println("Usage: carnivore SOURCE -o TARGET [-g] [-S] [-O] [--ir] [-c] [--verbose]");
		out.println();
	}

	public static void main(String args[]) {
		try {
			Config config = Config.parseArgs(args);
			Carnivore carnivore = new Carnivore(config);
			carnivore.compile(System.out);
		} catch (ConfigException ex) {
			System.err.println(ex.getMessage());
			printHelp(System.err);
			System.exit(2);
		} catch (CompileException ex) {
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}

	public static final String VERSION = "0.1";
	private Config config;
}

