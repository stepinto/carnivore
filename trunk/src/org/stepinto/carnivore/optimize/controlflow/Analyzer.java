package org.stepinto.carnivore.optimize.controlflow;

import java.util.*;
import org.stepinto.carnivore.ir.*;

public class Analyzer {
	public Analyzer(InsBuffer ibuf) {
		this.ibuf = ibuf;
	}

	public List<Block> findBlocks() {
		List<Block> blocks = new ArrayList<Block>();
		Map<Ins, Block> insBlock = new HashMap<Ins, Block>();

		// firstly, determine leaders and blocks
		List<Ins> leaders = findLeaders();
		for (Ins leader : leaders) {
			Block block = findBlockAt(leader, leaders);
			assert(block != null);
			blocks.add(block);

			for (Ins ins : block.getIns()) 
				insBlock.put(ins, block);
		}

		// fill exits of each block
		for (Block block : blocks) {
			for (Ins ins : block.getIns()) {
				if (ins instanceof JumpIfIns || ins instanceof JumpIns) {
					int target = (ins instanceof JumpIns ? ((JumpIns)ins).getTarget() : ((JumpIfIns)ins).getTarget());
					Ins targetIns = ibuf.getIns(target);
					Block targetBlock = insBlock.get(target);
					if (targetBlock != null)  // targetBlock == null => unreached ins
						targetBlock.getExits().add(new BlockExit(ins, targetIns));
				}
			}
		}

		// fill entries of each block
		for (Block block : blocks)
			for (BlockExit exit : block.getExits()) {
				Block nextBlock = insBlock.get(exit.getNextLeader());
				nextBlock.getEntries().add(new BlockEntry(exit.getCurrIns(), exit.getNextLeader()));
			}

		return blocks;
	}

	public Block findBlockAt(Ins leader, List<Ins> leaders) {
		int leaderIndex = ibuf.getIns().indexOf(leader);
		int insSize = ibuf.getIns().size();
		List<Ins> blockIns = new ArrayList<Ins>();

		blockIns.add(leader);
		if (leaderIndex < insSize-1) {   // contains more than one ins
			for (Ins ins : ibuf.getIns().subList(leaderIndex+1, insSize-1)) {
				if (leaders.contains(ins))
					break;
				blockIns.add(ins);
			}
		}

		return new Block(blockIns, new ArrayList<BlockEntry>(), new ArrayList<BlockExit>());
	}

	public List<Ins> findLeaders() {
		List<Ins> leaders = new ArrayList<Ins>();
		
		if (!ibuf.getIns().isEmpty()) {
			// the first ins is leader of course
			leaders.add(ibuf.getIns(0));

			// all ins after absolutely gotos are leaders
			/*Ins prevIns = null;
			for (Ins ins : ibuf.getIns()) {
				if (prevIns != null) {
					if (prevIns instanceof JumpIfIns || prevIns instanceof JumpIns)
						leaders.add(ins);
				}
				prevIns = ins;
			}*/

			// all target ins of gotos are leaders
			for (Ins ins : ibuf.getIns()) {
				if (ins instanceof JumpIfIns || ins instanceof JumpIns) {
					int target = (ins instanceof JumpIns ? ((JumpIns)ins).getTarget() : ((JumpIfIns)ins).getTarget());
					Ins targetIns = ibuf.getIns(target);
					leaders.add(targetIns);
				}
			}
		}
		return leaders;
	}

	private InsBuffer ibuf;
}

