package org.stepinto.carnivore.parser;
import java.util.*;

public class RecordInitExpList extends Exp {
	private List<RecordInitExp> list;

	public RecordInitExpList() { super(-1); }

	public List<RecordInitExp> getInitExps() {
		return list;
	}
}
