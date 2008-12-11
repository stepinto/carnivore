package org.stepinto.carnivore.arch;

import org.stepinto.carnivore.sematics.*;

public abstract class Arch {
	// create a frame given previous frame and a static-link variable (might be null)
	abstract public Frame newFrame(Frame parent, Variable staticLink);

	// return the size of a integer
	// in carnivore, integers and pointers MUST have same size
	abstract public int getIntSize();
}

