package org.stepinto.carnivore.sematics;

public class Identifier {
	public Identifier(String name) { this.name = name; uid = ++count; }
	public String getName() { return name; }
	public int getUid() { return uid; }

	private String name;
	private int uid;
	private static int count = 0;

	public boolean equals(Object obj) {
		if (obj instanceof Identifier)
			return this.getUid() == ((Identifier)obj).getUid();
		else
			return false;
	}
}

