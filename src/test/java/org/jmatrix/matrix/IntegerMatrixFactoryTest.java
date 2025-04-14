package org.jmatrix.matrix;

import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.matrixFactory.IntegerMatrixFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegerMatrixFactoryTest {

	@Test
	void testCreatingMatrixWithRandomValues() throws EmptyMatrixException, ListsIncompatibleForMatrixException {
		int rows = 50;
		int cols = 70;
		int minRange = -50;
		int maxRange = 50;
		Matrix<Integer> matrix = IntegerMatrixFactory.withRandomValues(rows, cols, minRange, maxRange);

		assertEquals(rows, matrix.getRows());
		assertTrue(matrix.getMatrixLists().stream().allMatch(list -> list.size() == cols),
				"Unexpected number of columns in matrix");

		assertTrue(matrix.getMatrixLists().stream().allMatch(
						list -> list.stream().allMatch((x -> x >= minRange && x <= maxRange))),
				"Matrix elements are not within min and max range");
	}

}