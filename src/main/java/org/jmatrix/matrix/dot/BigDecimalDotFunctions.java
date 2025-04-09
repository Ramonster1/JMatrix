package org.jmatrix.matrix.dot;

import java.math.BigDecimal;

//@ThreadSafe
public class BigDecimalDotFunctions extends DotHelperFunctions<BigDecimal> {

	// TODO Use Lazy initiazation instead, CIP page 348, or use AtomicReference updateAndGet

	private static volatile DotHelperFunctions<BigDecimal> instance;

	private BigDecimalDotFunctions() {
		super(BigDecimal::multiply, java.math.BigDecimal.ZERO, BigDecimal::add);
	}

	public static DotHelperFunctions<BigDecimal> getInstance() {
		// Implements double-checked locking
		DotHelperFunctions<BigDecimal> result = instance;
		if (result != null) {
			return result;
		}

		synchronized (BigDecimalDotFunctions.class) {
			if (instance == null) {
				instance = new BigDecimalDotFunctions();
			}
			return instance;
		}
	}
}
