package org.stepinto.carnivore.optimize;

import org.stepinto.carnivore.ir.*;

public abstract class Optimizer {
	abstract public InsBuffer optimize(InsBuffer oldIbuf);
}

