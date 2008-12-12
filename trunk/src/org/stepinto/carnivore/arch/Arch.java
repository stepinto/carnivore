package org.stepinto.carnivore.arch;

import java.util.*;
import java.io.*;
import org.stepinto.carnivore.sematics.*;
import org.stepinto.carnivore.ir.*;

public abstract class Arch {
	// create a frame given previous frame and a static-link variable (might be null)
	abstract public Frame newFrame(Frame parent, Variable staticLink);

	// create a code generator
	abstract public Generator newGenerator(Map<Function, InsBuffer> funcIns, List<String> strs, PrintStream out);



	// return the size of a integer
	// in carnivore, integers and pointers MUST have same size
	abstract public int getIntSize();
}

