package org.jmatrix.matrix.dot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntegerDotFunctionsTest {

	@Test
	void testFunctions() {
		assertEquals(20, IntegerDotFunctions.getInstance().getMultiplyFunction().apply(2, 10));
		assertEquals(12, IntegerDotFunctions.getInstance().getAccumulatorFunction().apply(2, 10));
		assertEquals(0, IntegerDotFunctions.getInstance().getZeroVal());
	}
}