package org.stepinto.carnivore.sematics;

import org.stepinto.carnivore.parser.*;
import org.stepinto.carnivore.arch.*;

public class Variable extends Identifier {
	public Variable(String id, Type type, boolean escape, Frame frame) {
		super(id);
		this.type = type;
		this.initExp = initExp;
		this.escape = escape;
		this.frame = frame;
	}

	public Variable(String id, Type type, Exp initExp, boolean escape, Frame frame) {
		super(id);
		this.type = type;
		this.initExp = initExp;
		this.escape = escape;
		this.frame = frame;
	}

	public Type getType() { return type; }
	public Exp getInitExp() { return initExp; }
	public boolean isEscape() { return escape; }
	public Frame getFrame() { return frame; }

	// this method may be accessed by TypeChecker, since whether a vaiable is escaped or 
	// not cannot be determined at the first sight of that variable
	void setEscape(boolean escape) { this.escape = escape; }

	private Type type;
	private Exp initExp;
	private boolean escape;
	private Frame frame;

	public String toString() {
		return "variable { name=" + getName() + ", type=" + type + " }";
	}
}

