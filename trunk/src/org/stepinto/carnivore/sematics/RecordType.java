package org.stepinto.carnivore.sematics;
import java.util.*;

public class RecordType extends Type {
	public RecordType(String name, Map<String, Type> fields) {
		super(name);
		fields.putAll(fields);
	}

	public boolean equals(Object obj) {
		if (obj instanceof RecordType)
			return getUid() == ((RecordType)obj).getUid();
		else if (obj instanceof NilType)
			return true; // nil can be assigned to any record type
		else
			return false;
	}

	public Map<String, Type> getFields() { return fields; }

	// this method could only be called in this package
	void setFields(Map<String, Type> fields) { this.fields.clear(); this.fields.putAll(fields); }

	private Map<String, Type> fields = new HashMap<String, Type>();
}

