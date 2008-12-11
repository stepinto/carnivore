package org.stepinto.carnivore.arch;

import java.util.*;
import org.stepinto.carnivore.sematics.*;

abstract public class Frame {
	public Frame(Frame parent, Variable staticLink) {
		this.parent = parent;
		this.staticLink = staticLink;
	}

	public Frame getParent() {
		return parent;
	}

	public Variable getStaticLink() {
		return staticLink;
	}

	public List<Variable> getLocals() {
		return locals;
	}

	public List<Variable> getParams() {
		return params;
	}

	private Frame parent;
	private List<Variable> locals = new ArrayList<Variable>();
	private List<Variable> params = new ArrayList<Variable>();
	private Variable staticLink;

	public void addLocal(Variable var) { locals.add(var); }
	public void addParam(Variable var) { params.add(var); }

	// test if this frame contains a variable
	public boolean contains(Variable var) {
		return locals.contains(var) || params.contains(var) || staticLink == var;
	}

	// get the offset of a variable
	abstract public int getOffset(Variable var);
}

