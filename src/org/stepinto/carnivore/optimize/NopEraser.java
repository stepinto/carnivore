package org.stepinto.carnivore.optimize;

import java.util.*;
import org.stepinto.carnivore.ir.*;

public class NopEraser extends Optimizer {
	public InsBuffer optimize(InsBuffer ibuf) {
		Map<Integer, Integer> lineMap = new HashMap<Integer, Integer>();
		InsBuffer newIbuf = new InsBuffer();

		// find "key" lines, i.e. target lines of gotos 
		for (Ins ins : ibuf.getIns()) {
			if (ins instanceof JumpIns) {
				int target = ((JumpIns)ins).getTarget();
				lineMap.put(target, target);
			}
			else if (ins instanceof JumpIfIns) {
				int target = ((JumpIfIns)ins).getTarget();
				lineMap.put(target, target);
			}
		}

		// find the new line-no of each key lines
		int newLineNo = 1;  // start from 1
		for (Ins ins : ibuf.getIns()) {
			int oldLineNo = ins.getLineNo();
			if (lineMap.containsKey(oldLineNo))
				lineMap.put(oldLineNo, newLineNo);
			if (!(ins instanceof NopIns))
				newLineNo++;
		}

		// construct a new ibuf and copy non-nop ins
		for (Ins ins : ibuf.getIns()) {
			if (ins instanceof JumpIns) {
				int oldTarget = ((JumpIns)ins).getTarget();
				int newTarget = lineMap.get(oldTarget);
				newIbuf.writeJumpIns(newTarget);
			}
			else if (ins instanceof JumpIfIns) {
				JumpIfIns jumpIfIns = (JumpIfIns)ins;
				int oldTarget = jumpIfIns.getTarget();
				int newTarget = lineMap.get(oldTarget);
				newIbuf.writeJumpIfIns(jumpIfIns.getRelOp(), jumpIfIns.getLeft(), jumpIfIns.getRight(), newTarget);
			}
			else if (ins instanceof NopIns) {
				// do nothing
			}
			else 
				newIbuf.writeIns(ins);
		}

		return newIbuf;
	}
}

