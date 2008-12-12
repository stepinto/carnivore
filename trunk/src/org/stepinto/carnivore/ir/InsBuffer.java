package org.stepinto.carnivore.ir;
import java.util.*;
import java.io.*;

public class InsBuffer {
	private class InsCompByLineNo implements Comparator<Ins> {
		public int compare(Ins left, Ins right) {
			return left.getLineNo() - right.getLineNo();
		}
		
		public boolean equals(Ins left, Ins right) {
			return left.getLineNo() == right.getLineNo();
		}
	}

	public void writeArthIns(int op, String result, String operand1, String operand2) {
		ins.add(new ArthIns(getCurrLineNo(), op, result, operand1, operand2));
	}

	public void writeAssignIns(String left, String right) {
		ins.add(new AssignIns(getCurrLineNo(), left, right));
	}

	public void writeMemReadIns(String left, String base, String index) {
		ins.add(new MemReadIns(getCurrLineNo(), left, base, index));
	}

	public void writeMemWriteIns(String base, String index, String right) {
		ins.add(new MemWriteIns(getCurrLineNo(), base, index, right));
	}

	public void writeCallIns(String target) {
		ins.add(new CallIns(getCurrLineNo(), target));
	}

	public void writeCallIns(String left, String target) {
		ins.add(new CallIns(getCurrLineNo(), left, target));
	}

	public void writeJumpIfIns(int relOp, String operand1, String operand2, int target) {
		ins.add(new JumpIfIns(getCurrLineNo(), relOp, operand1, operand2, target));
	}

	public void writeJumpIns(int target) {
		ins.add(new JumpIns(getCurrLineNo(), target));
	}

	public void writeParamIns(String param) {
		ins.add(new ParamIns(getCurrLineNo(), param));
	}

	public void writeNopIns() {
		ins.add(new NopIns(getCurrLineNo()));
	}

	public void writeFakeJumpIns(String target) {
		ins.add(new FakeJumpIns(getCurrLineNo(), target));
	}

	public void writeFakeJumpIfIns(int relOp, String operand1, String operand2, String target) {
		ins.add(new FakeJumpIfIns(getCurrLineNo(), relOp, operand1, operand2, target));
	}

	public void writeRetIns() {
		ins.add(new RetIns(getCurrLineNo()));
	}

	public void writeRetIns(String var) {
		ins.add(new RetIns(getCurrLineNo(), var));
	}

	public void patchFakeJump(String label, int target) {
		ListIterator<Ins> iter = ins.listIterator();
		while (iter.hasNext()) {
			Ins i = iter.next();
			if (i instanceof FakeJumpIns) {
				FakeJumpIns fj = (FakeJumpIns)i;
				if (fj.getTarget().equals(label))
					iter.set(new JumpIns(i.getLineNo(), target));
			}
			else if (i instanceof FakeJumpIfIns) {
				FakeJumpIfIns fji = (FakeJumpIfIns)i;
				if (fji.getTarget().equals(label))
					iter.set(new JumpIfIns(i.getLineNo(), fji.getRelOp(), fji.getOperand1(),
								fji.getOperand2(), target));
			}
		}
	}

	public void dump(PrintStream out) {
		Collections.sort(ins, new InsCompByLineNo());
		for (Ins i: ins)
			out.println(i);
	}

	public void dump() {
		dump(System.out);
	}

	public int getCurrLineNo() {
		return ins.size() + 1;
	}

	private List<Ins> ins = new ArrayList<Ins>();
}

