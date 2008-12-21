package org.stepinto.carnivore;

import java.io.*;

public class AssemblerProxy {
	public AssemblerProxy(File asm, File obj, boolean debug) {
		this.asm = asm;
		this.obj = obj;
		this.debug = debug;
	}

	public void assemble() throws AssemblerException {
		Runtime runtime = Runtime.getRuntime();
		String cmd = "nasm " + asm.getAbsolutePath() + " -o " + obj.getAbsolutePath()
			+ (debug ? " -g" : "");
		if (getOS() == OS_LINUX)
			cmd = cmd + " -f elf";
		else if (getOS() == OS_WIN32)
			cmd = cmd + " -f win32";
		else
			throw new AssemblerException();

		try {
			Process process = runtime.exec(cmd);
			process.waitFor();
			if (process.exitValue() != 0)
				throw new AssemblerException();
		} catch (Exception ex) {
			throw new AssemblerException();
		}
	}

	private int getOS() {
		String osName = System.getProperties().getProperty("os.name");
		if (osName.indexOf("Linux") != -1)
			return OS_LINUX;
		else if (osName.indexOf("Windows") != -1)
			return OS_WIN32;
		else
			return OS_UNKNOWN;
	}

	private int OS_LINUX = 1;
	private int OS_WIN32 = 2;
	private int OS_UNKNOWN = 3;

	private File asm;
	private File obj;
	private boolean debug;
}

