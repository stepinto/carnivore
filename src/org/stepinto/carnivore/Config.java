package org.stepinto.carnivore;

import java.io.*;


public class Config {
	public Config(File source, File target, boolean debug, boolean verbose, boolean optimized,
			int targetCPU, int targetCode) {
		this.source = source;
		this.target = target;
		this.debug = debug;
		this.verbose = verbose;
		this.optimized = optimized;
		this.targetCPU = targetCPU;
		this.targetCode = targetCode;
	}

	public static Config parseArgs(String args[]) throws ConfigException {
		String inPath = null;
		String outPath = null;
		boolean debug = false;
		boolean verbose = false;
		boolean optimized = false;
		int targetCPU = TCPU_X86;
		int targetCode = TC_EXE;

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals("--debug") || args.equals("-g"))
				debug = true;
			else if (arg.equals("--verbose"))
				verbose = true;
			else if (arg.equals("--optimize") || args.equals("-O"))
				optimized = true;
			else if (arg.equals("--amd64"))
				targetCPU = TCPU_AMD64;
			else if (arg.equals("-S"))
				targetCode = TC_ASM;
			else if (arg.equals("-c"))
				targetCode = TC_OBJ;
			else if (arg.equals("--ir"))
				targetCode = TC_IR;
			else if (arg.equals("-o")) {
				i++;
				if (outPath != null)
					throw new ConfigException("Multiple -o switchs.");
				if (i >= args.length)
					throw new ConfigException("Expect a file path.");
				outPath = args[i];
			}
			else {
				if (inPath != null)
					throw new ConfigException("Multiple sources.");
				inPath = arg;
			}
		}

		if (outPath == null) 
			outPath = "a.out";
		if (inPath == null)
			throw new ConfigException("Need source.");

		return new Config(new File(inPath), new File(outPath), debug, verbose, optimized, 
				targetCPU, targetCode);
	}

	public void dump(PrintStream out) {
		out.println("Source:\t" + source.getPath());
		out.println("Target:\t" + target.getPath());
		out.println("Debug:\t" + (debug ? "Yes" : "No"));
		out.println("Verbose:\t" + (verbose ? "Yes" : "No"));
		out.println("Target-CPU:\t" + getTargetCPUName());
		out.println("Target-Code:\t" + getTargetCodeName());
	}

	public String getTargetCPUName() {
		if (targetCPU == TCPU_X86)
			return "Intel X86";
		else if (targetCPU == TCPU_AMD64)
			return "Intel EMT-64 or AMD-64";
		else {
			assert(false);
			return "";
		}
	}

	public String getTargetCodeName() {
		if (targetCode == TC_EXE)
			return "Executable";
		else if (targetCode == TC_ASM)
			return "Assembly";
		else if (targetCode == TC_OBJ)
			return "Object";
		else if (targetCode == TC_IR)
			return "Intermediate Representation";
		else {
			assert(false);
			return "";
		}
	}

	public File getSource() { return source; }
	public File getTarget() { return target; }
	public boolean isDebug() { return debug; }
	public boolean isVerbose() { return verbose; }
	public boolean isOptimized() { return optimized; }
	public int getTargetCPU() { return targetCPU; }
	public int getTargetCode() { return targetCode; }

	private File source;
	private File target;
	private boolean debug;
	private boolean verbose;
	private boolean optimized;
	private int targetCPU;
	private int targetCode;

	public static final int TCPU_X86 = 1;
	public static final int TCPU_AMD64 = 2;

	public static final int TC_EXE = 1;
	public static final int TC_ASM = 2;
	public static final int TC_OBJ = 3;
	public static final int TC_IR = 4;
}

