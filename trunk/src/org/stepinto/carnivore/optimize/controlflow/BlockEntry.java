package org.stepinto.carnivore.optimize.controlflow;

import java.util.*;
import org.stepinto.carnivore.ir.*;

public class BlockEntry {
	public BlockEntry(Ins lastIns, Ins currLeader) {
		this.lastIns = lastIns;
		this.currLeader = currLeader;
	}

	public Ins getLastIns() { return lastIns; }
	public Ins getCurrLeader() { return currLeader; }

	private Ins lastIns;
	private Ins currLeader;
}

