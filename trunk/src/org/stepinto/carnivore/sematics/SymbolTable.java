package org.stepinto.carnivore.sematics;
import java.util.*;

public class SymbolTable {
	public SymbolTable() {
		push();
	}

	public void push() {
		stack.add(new HashMap<String, Identifier>());
	}

	public void pop() {
		stack.remove(stack.size()-1);
	}

	public Identifier lookup(String name) {
		for (int i = stack.size()-1; i >= 0; i--) {
			Map<String, Identifier> map = stack.get(i);
			Identifier ret = map.get(name);
			if (ret != null)
				return ret;
		}
		return null;
	}

	public Identifier lookupLocal(String name) {
		return stack.get(stack.size()-1).get(name);
	}

	public void put(Identifier id) {
		assert(!stack.isEmpty());
		stack.get(stack.size()-1).put(id.getName(), id);
	}

	ArrayList<Map<String, Identifier>> stack = new ArrayList<Map<String, Identifier>>();
}

