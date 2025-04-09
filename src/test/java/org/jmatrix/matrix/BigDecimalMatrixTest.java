package org.jmatrix.matrix;

import org.jmatrix.matrix.dot.BigDecimalDotFunctions;
import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.exception.MatricesIncompatibleForOperationException;
import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.matrixFactory.BigDecimalMatrixFactoryImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BigDecimalMatrixTest {

	@Test
	void testSerialAndParallelDotResultsAreSame() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		var matrixFactory = new BigDecimalMatrixFactoryImpl(BigDecimalDotFunctions.getInstance());
		var matrix1 = matrixFactory.withRandomValues(200, 784, BigDecimal.valueOf(-0.5), BigDecimal.valueOf(0.5));
		var matrix2 = matrixFactory.withRandomValues(784, 1, BigDecimal.valueOf(-0.5), BigDecimal.valueOf(0.5));

		var serialResult = matrix1.parallelDot(matrix2);
		var concurrentResult = matrix1.parallelDot(matrix2);
		var forkJoinResult = matrix1.forkJoinDot(matrix2, 20);

		assertEquals(serialResult, concurrentResult);
		assertEquals(serialResult, forkJoinResult);
	}

	@Test
	void testSerialAndParallelCombineResultsAreSame() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		var matrixFactory = new BigDecimalMatrixFactoryImpl(BigDecimalDotFunctions.getInstance());
		var matrix1 = matrixFactory.withRandomValues(200, 784, BigDecimal.valueOf(-0.5), BigDecimal.valueOf(0.5));
		var matrix2 = matrixFactory.withRandomValues(200, 784, BigDecimal.valueOf(-0.5), BigDecimal.valueOf(0.5));

		var serialResult = matrix1.combine(matrix2, BigDecimal::multiply);
		var concurrentResult = matrix1.combine(matrix2, BigDecimal::multiply);
		var forkAndJoinResult = matrix1.forkAndJoinCombine(matrix2, BigDecimal::multiply, 5000);

		assertEquals(serialResult, concurrentResult);
		assertEquals(serialResult, forkAndJoinResult);
	}

	@Test
	void testDotProductResults() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		int MATRIX1_ROWS = 100;
		int MATRIX2_ROWS = 100;
		int MATRIX1_COLS = 100;
		int MATRIX2_COLS = 100;

		Matrix<BigDecimal> matrix1 = new Matrix<>(
				BigDecimalDotFunctions.getInstance(),
				IntStream.rangeClosed(1, MATRIX1_ROWS).mapToObj(i -> Collections.nCopies(MATRIX1_COLS, BigDecimal.valueOf(i))).collect(Collectors.toList())
		);

		Matrix<BigDecimal> matrix2 = new Matrix<>(
				BigDecimalDotFunctions.getInstance(),
				IntStream.rangeClosed(1, MATRIX2_ROWS)
						.mapToObj(i -> IntStream.rangeClosed(1, MATRIX2_COLS).mapToObj(BigDecimal::valueOf).collect(Collectors.toList())
						).collect(Collectors.toList())
		);

		List<List<BigDecimal>> resultList = new ArrayList<>();

		for (var i = 1; i <= MATRIX1_ROWS; i++) {
			final int finalI = i;
			resultList.add(Stream.iterate(
					BigDecimal.valueOf((long) MATRIX1_ROWS * i),
					n -> n.add(BigDecimal.valueOf((long) MATRIX1_ROWS * finalI))).limit(MATRIX1_ROWS).collect(Collectors.toList()));
		}

		Matrix<BigDecimal> expectedResultMatrix = new Matrix<>(BigDecimalDotFunctions.getInstance(), resultList);

		Matrix<BigDecimal> serialResultMatrix = matrix1.dot(matrix2);
		Matrix<BigDecimal> parallelResultMatrix = matrix1.parallelDot(matrix2);

		assertEquals(expectedResultMatrix, serialResultMatrix);
		assertEquals(expectedResultMatrix, parallelResultMatrix);
	}

	@Test
	void testMatrixSubtraction() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		var matrix1 = new Matrix<>(BigDecimalDotFunctions.getInstance(), List.of(
				List.of(BigDecimal.valueOf(7), BigDecimal.valueOf(8)),
				List.of(BigDecimal.valueOf(9), BigDecimal.valueOf(10)),
				List.of(BigDecimal.valueOf(11), BigDecimal.valueOf(12))));

		var matrix2 = new Matrix<>(BigDecimalDotFunctions.getInstance(), List.of(
				List.of(BigDecimal.valueOf(7), BigDecimal.valueOf(8)),
				List.of(BigDecimal.valueOf(10), BigDecimal.valueOf(12)),
				List.of(BigDecimal.valueOf(14), BigDecimal.valueOf(16))));

		var expectedResultMatrix = new Matrix<>(BigDecimalDotFunctions.getInstance(), List.of(
				List.of(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
				List.of(BigDecimal.valueOf(-1), BigDecimal.valueOf(-2)),
				List.of(BigDecimal.valueOf(-3), BigDecimal.valueOf(-4))));

		var actualResultMatrix = matrix1.combine(matrix2, BigDecimal::subtract);

		assertEquals(2, actualResultMatrix.getColumns());
		assertEquals(3, actualResultMatrix.getRows());
		assertEquals(actualResultMatrix, expectedResultMatrix);
	}
}