package org.stepinto.carnivore.sematics;

public class Type extends Identifier {
	public Type(String name) {
		super(name);
	}

	public String toString() {
		return "type { name=" + getName() +  "}";
	}
}

