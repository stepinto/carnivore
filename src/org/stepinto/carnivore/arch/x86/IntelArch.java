package org.stepinto.carnivore.arch.x86;

import org.stepinto.carnivore.arch.*;
import org.stepinto.carnivore.sematics.*;

public class IntelArch extends Arch {
	public static final int INT_SIZE = 4;
	private static IntelArch instance = new IntelArch();

	public Frame newFrame(Frame parent, Variable staticLink) {
		return new X86Frame((X86Frame)parent, staticLink);
	}

	public int getIntSize() {
		return INT_SIZE;
	}

	public static IntelArch getInstance() {
		return instance;
	}
}

