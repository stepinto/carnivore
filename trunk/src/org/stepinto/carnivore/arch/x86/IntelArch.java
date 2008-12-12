package org.stepinto.carnivore.arch.x86;

import java.util.*;
import java.io.*;
import org.stepinto.carnivore.arch.*;
import org.stepinto.carnivore.sematics.*;
import org.stepinto.carnivore.ir.*;

public class IntelArch extends Arch {
	public static final int INT_SIZE = 4;
	private static IntelArch instance = new IntelArch();

	public Frame newFrame(Frame parent, Variable staticLink) {
		return new X86Frame((X86Frame)parent, staticLink);
	}

	public Generator newGenerator(Map<Function, InsBuffer> funcIns, List<String> strs, Function entryFunc, 
			PrintStream out) {
		return new X86Generator(funcIns, strs, entryFunc, out);
	}

	public int getIntSize() {
		return INT_SIZE;
	}

	public static IntelArch getInstance() {
		return instance;
	}
}

