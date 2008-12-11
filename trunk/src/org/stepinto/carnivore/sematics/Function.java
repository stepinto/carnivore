package org.stepinto.carnivore.sematics;

import java.util.*;
import org.stepinto.carnivore.parser.*;
import org.stepinto.carnivore.arch.*;

public class Function extends Identifier {
	public Function(String name, List<Variable> args, Type ret, Exp body, Frame frame) {
		super(name);
		this.args = args;
		this.ret = ret;
		this.body = body;
		this.frame = frame;
	}

	public List<Variable> getArgs() { return args; }
	public Type getRetType() { return ret; }
	public Exp getBody() { return body; }
	public boolean isExternal() { return body == null; }
	public Frame getFrame() { return frame; }

	// only can be called by functions in this package
	void setBody(Exp body) { this.body = body; }

	public String toString() {
		return "function { name=" + getName() + " }";
	}

	private List<Variable> args;
	private Type ret;
	private Exp body;
	private Frame frame;
}

