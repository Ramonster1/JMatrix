package org.jmatrix.matrix.matrix.calculator;

import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.exception.MatricesIncompatibleForOperationException;
import org.jmatrix.matrix.matrix.Matrix;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MatricesCalculatorCombineTest {

	/**
	 * Tests the combine method of MatricesCalculator which combines two matrices
	 * using the provided combine function.
	 */

	@Test
	public void testCombine_validMatrices() throws EmptyMatrixException, ListsIncompatibleForMatrixException, MatricesIncompatibleForOperationException {
		Matrix<Integer> matrix1 = new Matrix<>(List.of(
				List.of(1, 2, 3),
				List.of(4, 5, 6),
				List.of(7, 8, 9)
		));

		Matrix<Integer> matrix2 = new Matrix<>(List.of(
				List.of(9, 8, 7),
				List.of(6, 5, 4),
				List.of(3, 2, 1)
		));

		Matrix<Integer> expected = new Matrix<>(List.of(
				List.of(10, 10, 10),
				List.of(10, 10, 10),
				List.of(10, 10, 10)
		));

		Matrix<Integer> result = MatricesCalculator.combine(matrix1, matrix2, Integer::sum);

		assertEquals(expected, result);
	}

	@Test
	public void testParallelCombine() throws EmptyMatrixException, ListsIncompatibleForMatrixException, MatricesIncompatibleForOperationException {
		Matrix<Integer> matrix1 = new Matrix<>(List.of(
				List.of(1, 2, 3),
				List.of(4, 5, 6),
				List.of(7, 8, 9)
		));

		Matrix<Integer> matrix2 = new Matrix<>(List.of(
				List.of(9, 8, 7),
				List.of(6, 5, 4),
				List.of(3, 2, 1)
		));

		Matrix<Integer> result = MatricesCalculator.parallelCombine(matrix1, matrix2, Integer::sum);

		Matrix<Integer> expected = new Matrix<>(List.of(
				List.of(10, 10, 10),
				List.of(10, 10, 10),
				List.of(10, 10, 10)
		));

		assertEquals(expected, result);
	}

	@Test
	public void testMultiProcessorCombine() throws EmptyMatrixException, ListsIncompatibleForMatrixException, MatricesIncompatibleForOperationException {
		Matrix<Integer> matrix1 = new Matrix<>(List.of(
				List.of(1, 2, 3),
				List.of(4, 5, 6),
				List.of(7, 8, 9)
		));

		Matrix<Integer> matrix2 = new Matrix<>(List.of(
				List.of(9, 8, 7),
				List.of(6, 5, 4),
				List.of(3, 2, 1)
		));

		Matrix<Integer> result = MatricesCalculator.parallelCombine(matrix1, matrix2, Integer::sum, 2);

		Matrix<Integer> expected = new Matrix<>(List.of(
				List.of(10, 10, 10),
				List.of(10, 10, 10),
				List.of(10, 10, 10)
		));

		assertEquals(expected, result);
	}

	@Test
	public void testCombine_MatricesDifferentColumnSizes_ThrowsMatricesIncompatibleForOperationException() throws EmptyMatrixException, ListsIncompatibleForMatrixException {
		Matrix<Integer> matrix1 = new Matrix<>(List.of(
				List.of(1, 2),
				List.of(3, 4)
		));

		Matrix<Integer> matrix2 = new Matrix<>(List.of(
				List.of(5, 6, 7),
				List.of(8, 9, 10)
		));

		MatricesIncompatibleForOperationException e =
				assertThrows(MatricesIncompatibleForOperationException.class, () -> {
					MatricesCalculator.combine(matrix1, matrix2, Integer::sum);
				});

		assertEquals("Cannot apply combine function with other matrix. Columns and rows do not match.",
				e.getMessage());
	}

	@Test
	public void testCombine_MatricesDifferentRowSizes_ThrowsListsIncompatibleForMatrixException() {
		Matrix<Integer> matrix1 = new Matrix<>(List.of(
				List.of(1, 2),
				List.of(3, 4)
		));

		Matrix<Integer> matrix2 = new Matrix<>(List.of(
				List.of(5, 6),
				List.of(8, 9),
				List.of(8, 9)
		));

		MatricesIncompatibleForOperationException e =
				assertThrows(MatricesIncompatibleForOperationException.class, () -> {
					MatricesCalculator.combine(matrix1, matrix2, Integer::sum);
				});

		assertEquals("Cannot apply combine function with other matrix. Columns and rows do not match.",
				e.getMessage());
	}
}