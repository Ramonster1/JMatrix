package org.jmatrix.matrix;

import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.matrixFactory.BigDecimalMatrixFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BigDecimalMatrixFactoryImplTest {

	@Test
	void testCreatingMatrixWithRandomValues() throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		int rows = 50;
		int cols = 70;
		BigDecimal minRange = BigDecimal.valueOf(-0.5);
		BigDecimal maxRange = BigDecimal.valueOf(0.5);
		Matrix<BigDecimal> matrix = BigDecimalMatrixFactory.withRandomValues(rows, cols, minRange, maxRange);

		assertEquals(rows, matrix.getMatrixLists().size());
		assertTrue(matrix.getMatrixLists().stream().allMatch(list -> list.size() == cols),
				"Unexpected number of columns in matrix");

		assertTrue(matrix.getMatrixLists().stream().allMatch(
						list -> list.stream().allMatch(
								x -> x.compareTo(minRange) >= 0 && x.compareTo(maxRange) <= 0)),
				"Matrix elements are not within min and max range");
	}
}