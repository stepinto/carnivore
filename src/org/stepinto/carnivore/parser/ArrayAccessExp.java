package org.stepinto.carnivore.parser;

public class ArrayAccessExp extends Exp {
	public ArrayAccessExp(int lineNo, Exp id, Exp index) {
		super(lineNo);
		this.id = id;
		this.index = index;
	}

	public Exp getId() { return id; }
	public Exp getIndexExp() { return index; }
	private Exp id, index;
}
