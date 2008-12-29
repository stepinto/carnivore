package org.stepinto.carnivore.optimize.controlflow;

import java.util.*;
import org.stepinto.carnivore.ir.*;

public class Block {
	public Block(List<Ins> ins, List<BlockEntry> entries, List<BlockExit> exits) {
		this.ins = ins;
		this.entries = entries;
		this.exits = exits;
	}

	public List<Ins> getIns() { return ins; }
	public List<BlockEntry> getEntries() { return entries; }
	public List<BlockExit> getExits() { return exits; }

	private List<Ins> ins;
	private List<BlockEntry> entries;
	private List<BlockExit> exits;
}

