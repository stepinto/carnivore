package org.stepinto.carnivore.optimize.controlflow;

import org.stepinto.carnivore.ir.*;

public class BlockExit {
	public BlockExit(Ins currIns, Ins nextLeader) {
		this.currIns = currIns;
		this.nextLeader = nextLeader;
	}

	public Ins getCurrIns() { return currIns; }
	public Ins getNextLeader() { return nextLeader; }

	private Ins currIns;
	private Ins nextLeader;
}

