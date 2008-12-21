package org.stepinto.carnivore;

import java.io.*;
import java.util.*;

public class LinkerProxy {
	public LinkerProxy(List<File> objs, File exe, boolean debug) {
		this.objs = objs;
		this.exe = exe;
		this.debug = debug;
	}

	public void link() throws LinkerException {
		StringBuffer cmd = new StringBuffer();

		// build cmd line
		cmd.append("gcc");
		for (File obj : objs)
			cmd.append(" " + obj.getAbsolutePath());
		cmd.append(" -o " + exe.getAbsolutePath());

		if (debug)
			cmd.append(" -g");
	
		// run the process
		try {
			Process process = Runtime.getRuntime().exec(cmd.toString());
			process.waitFor();
			if (process.exitValue() != 0)
				throw new LinkerException();
		} catch (Exception ex) {
			throw new LinkerException();
		}
	}

	private List<File> objs;
	private File exe;
	private boolean debug;
}

