package org.stepinto.carnivore.sematics;

public class NilType extends Type {
	private NilType() { super("nil type"); }

	public boolean equals(Object obj) {
		// nil can be assigned to any record type
		return (obj instanceof RecordType) || (obj instanceof NilType);
	}

	public static NilType getInstance() { return instance; }
	private static NilType instance = new NilType();
}
