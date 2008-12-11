package org.stepinto.carnivore.arch.x86;

import org.stepinto.carnivore.sematics.*;
import org.stepinto.carnivore.arch.*;

public class X86Frame extends Frame {
	public X86Frame(X86Frame parent, Variable staticLink) {
		super(parent, staticLink);
	}

	public int getOffset(Variable var) {
		assert(contains(var));

		if (getStaticLink() == var)
		       return 8;
		else if (getLocals().contains(var)) {
			int offset = 0;
			for (Variable l: getLocals()) {
				offset -= IntelArch.INT_SIZE;
				if (l == var)
					return offset;
			}
			assert(false);
			return 0;
		}
		else {
			assert(getParams().contains(var));
			int offset = 8;
			for (Variable p: getParams()) {
				offset += IntelArch.INT_SIZE;
				if (p == var)
					return offset;
			}
			assert(false);
			return 0;
		}
	}
}

