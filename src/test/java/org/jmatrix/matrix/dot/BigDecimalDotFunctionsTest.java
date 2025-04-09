package org.jmatrix.matrix.dot;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BigDecimalDotFunctionsTest {

	@Test
	void testFunctions() {
		assertEquals(BigDecimal.valueOf(20), BigDecimalDotFunctions.getInstance().getMultiplyFunction().apply(BigDecimal.TWO, BigDecimal.TEN));
		assertEquals(BigDecimal.valueOf(12), BigDecimalDotFunctions.getInstance().getAccumulatorFunction().apply(BigDecimal.TWO, BigDecimal.TEN));
		assertEquals(BigDecimal.ZERO, BigDecimalDotFunctions.getInstance().getZeroVal());
	}
}