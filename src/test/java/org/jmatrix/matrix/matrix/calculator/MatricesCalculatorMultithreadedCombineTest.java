package org.jmatrix.matrix.matrix.calculator;

import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.exception.MatricesIncompatibleForOperationException;
import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.matrixFactory.BigDecimalMatrixFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MatricesCalculatorMultithreadedCombineTest {

	@Test
	public void testParallelCombine_SmallValidMatrices_ReturnsCombinedMatrix() throws EmptyMatrixException, ListsIncompatibleForMatrixException, MatricesIncompatibleForOperationException {
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

		Matrix<Integer> parallelCombineResult = MatricesCalculator.parallelCombine(matrix1, matrix2, Integer::sum);
		Matrix<Integer> multiThreadCombineResult = MatricesCalculator.parallelCombine(matrix1, matrix2, Integer::sum, 2);
		Matrix<Integer> forkJoinResult = MatricesCalculator.forkAndJoinCombine(matrix1, matrix2, Integer::sum, 1);

		assertEquals(expected, parallelCombineResult, "Parallel combine should be the same as multi-thread combine.");
		assertEquals(expected, multiThreadCombineResult, "Multi-thread combine should be the same as fork-join combine.");
		assertEquals(expected, forkJoinResult, "Fork-join combine should be the same as parallel combine.");
	}

	@Test
	void testLargeMatricesCombine_ReturnsSameMatrix() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		var matrix1 = BigDecimalMatrixFactory.withRandomValues(200, 784, BigDecimal.valueOf(-0.5), BigDecimal.valueOf(0.5));
		var matrix2 = BigDecimalMatrixFactory.withRandomValues(200, 784, BigDecimal.valueOf(-0.5), BigDecimal.valueOf(0.5));

		var serialResult = MatricesCalculator.combine(matrix1, matrix2, BigDecimal::multiply);
		var concurrentResult = MatricesCalculator.combine(matrix1, matrix2, BigDecimal::multiply);
		var forkAndJoinResult = MatricesCalculator.forkAndJoinCombine(matrix1, matrix2, BigDecimal::multiply, 50);

		assertEquals(serialResult, concurrentResult, "Serial and concurrent combine should be the same.");
		assertEquals(serialResult, forkAndJoinResult, "Serial and fork-join combine should be the same.");
	}

	@Test
	void testForkAndJoinCombineWithDifferentThresholds_ReturnsSameMatrix() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		var matrix1 = BigDecimalMatrixFactory.withRandomValues(200, 784, BigDecimal.valueOf(-0.5), BigDecimal.valueOf(0.5));
		var matrix2 = BigDecimalMatrixFactory.withRandomValues(200, 784, BigDecimal.valueOf(-0.5), BigDecimal.valueOf(0.5));

		var serialResult = MatricesCalculator.combine(matrix1, matrix2, BigDecimal::multiply);
		var smallThresholdResult = MatricesCalculator.forkAndJoinCombine(matrix1, matrix2, BigDecimal::multiply, 1);
		var largeThresholdResult = MatricesCalculator.forkAndJoinCombine(matrix1, matrix2, BigDecimal::multiply, 5000);

		assertEquals(serialResult, smallThresholdResult, "Serial and fork-join with small threshold combine should be the same.");
		assertEquals(serialResult, largeThresholdResult, "Serial and fork-join with large threshold combine should be the same.");
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
	public void testParallelCombine_MatricesDifferentColumnSizes_ThrowsMatricesIncompatibleForOperationException() throws EmptyMatrixException, ListsIncompatibleForMatrixException {
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
					MatricesCalculator.parallelCombine(matrix1, matrix2, Integer::sum);
				});

		assertEquals("Cannot apply combine function with other matrix. Columns and rows do not match.",
				e.getMessage());
	}

	@Test
	public void testMultiProcessorCombine_MatricesDifferentColumnSizes_ThrowsMatricesIncompatibleForOperationException() throws EmptyMatrixException, ListsIncompatibleForMatrixException {
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
					MatricesCalculator.parallelCombine(matrix1, matrix2, Integer::sum, 2);
				});

		assertEquals("Cannot apply combine function with other matrix. Columns and rows do not match.",
				e.getMessage());
	}


}