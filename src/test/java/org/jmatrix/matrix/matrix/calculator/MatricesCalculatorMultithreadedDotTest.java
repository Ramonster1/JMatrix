package org.jmatrix.matrix.matrix.calculator;

import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.matrixFactory.IntegerMatrixFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatricesCalculatorMultithreadedDotTest {


	/**
	 * Creates a pair of predictable matrices specifically designed for dot product computations.
	 * <p>
	 * This method uses an algorithm to create a predictable dot product result of any size.
	 * Matrix 1: Each value is equal to the row number. For example, when there's 100 rows and columns:
	 * Row 1: [1, 1, 1, ..., 1] (100 elements, all 1)
	 * Row 2: [2, 2, 2, ..., 2] (100 elements, all 2)
	 * ...
	 * Row 100: [100, 100, 100, ..., 100]
	 * <p>
	 * Matrix 2:  Each value is equal to the column number. For example, when there's 100 rows and columns:
	 * Row 1: [1, 2, 3, ..., 100]
	 * Row 2: [1, 2, 3, ..., 100]
	 * ...
	 * Row 100: [1, 2, 3, ..., 100]
	 * <p>
	 * Result of dot product: Each value can be calculated by rowNo * colNo * rowsColumnsCount, where
	 * rowNo = row number (starting from 1, 1st row is the List's index 0)
	 * colNo = column number (starting from 1)
	 * For example, when there's 100 rows and columns:
	 * Row 1 Column 1 value is: 100 (1 * 1 * 100)
	 * Row 1 Column 5 value is: 500 (1 * 5 * 100)
	 * Row 2 Column 2 value is: 400 (2 * 2 * 100)
	 * Row 50 Column 50 value is: 250000 (50 * 50 * 100)
	 *
	 * @param rowsColumnsCount the number of rows and columns for both matrices
	 * @return a list containing the matrices used for testing in the order: matrix1, matrix2, expectedResultMatrix
	 */
	static List<Matrix<BigDecimal>> createPredictableBigDecimalMatricesForDotProduct(int rowsColumnsCount) {
		Matrix<BigDecimal> matrix1 = new Matrix<>(
				IntStream.rangeClosed(1, rowsColumnsCount).mapToObj(i -> Collections.nCopies(rowsColumnsCount, BigDecimal.valueOf(i))).collect(Collectors.toList())
		);

		Matrix<BigDecimal> matrix2 = new Matrix<>(
				IntStream.rangeClosed(1, rowsColumnsCount)
						.mapToObj(i -> IntStream.rangeClosed(1, rowsColumnsCount).mapToObj(BigDecimal::valueOf).collect(Collectors.toList())
						).collect(Collectors.toList())
		);

		List<List<BigDecimal>> expectedResultMatrix = new ArrayList<>();

		for (var i = 1; i <= rowsColumnsCount; i++) {
			final int finalI = i;
			expectedResultMatrix.add(Stream.iterate(
					BigDecimal.valueOf((long) rowsColumnsCount * i),
					n -> n.add(BigDecimal.valueOf((long) rowsColumnsCount * finalI))).limit(rowsColumnsCount).collect(Collectors.toList()));
		}

		return List.of(matrix1, matrix2, new Matrix<>(expectedResultMatrix));
	}

	@Test
	void testLargeDotProductResults() {
		var matrix1 = IntegerMatrixFactory.withRandomValues(200, 784, Integer.valueOf(-5), Integer.valueOf(5));
		var matrix2 = IntegerMatrixFactory.withRandomValues(784, 200, Integer.valueOf(-5), Integer.valueOf(5));

		var serialResult = IntegerMatricesCalculator.dot(matrix1, matrix2);
		var concurrentResult = IntegerMatricesCalculator.parallelDot(matrix1, matrix2);
		var twoThreadedResult = IntegerMatricesCalculator.parallelDot(matrix1, matrix2, 2);
		var forkAndJoinResult = IntegerMatricesCalculator.forkJoinDot(matrix1, matrix2, 50);

		assertEquals(serialResult, concurrentResult, "Parallel dot product result is not correct.");
		assertEquals(serialResult, twoThreadedResult, "Multi-thread dot product result with 2 threads is not correct.");
		assertEquals(serialResult, forkAndJoinResult, "Fork/Join dot product result is not correct.");
	}

	@Test
	void testDotProductResults() {
		List<Matrix<BigDecimal>> testMatrices = createPredictableBigDecimalMatricesForDotProduct(100);

		Matrix<BigDecimal> matrix1 = testMatrices.get(0);
		Matrix<BigDecimal> matrix2 = testMatrices.get(1);
		Matrix<BigDecimal> expectedResultMatrix = testMatrices.get(2);

		Matrix<BigDecimal> serialResultMatrix = BigDecimalMatricesCalculator.dot(matrix1, matrix2);
		Matrix<BigDecimal> parallelResultMatrix = BigDecimalMatricesCalculator.parallelDot(matrix1, matrix2);
		Matrix<BigDecimal> multiThreadResultMatrix = BigDecimalMatricesCalculator.parallelDot(matrix1, matrix2, 2);
		Matrix<BigDecimal> forkJoinResultMatrix = BigDecimalMatricesCalculator.forkJoinDot(matrix1, matrix2, 1);

		assertEquals(expectedResultMatrix, serialResultMatrix, "Serial dot product result is not correct.");
		assertEquals(expectedResultMatrix, parallelResultMatrix, "Parallel dot product result is not correct.");
		assertEquals(expectedResultMatrix, multiThreadResultMatrix, "Multi-thread dot product result with 2 threads is not correct.");
		assertEquals(expectedResultMatrix, forkJoinResultMatrix, "Fork/Join dot product result is not correct.");
	}
}
