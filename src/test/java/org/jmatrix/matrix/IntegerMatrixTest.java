package org.jmatrix.matrix;

import org.jmatrix.matrix.dot.IntegerDotFunctions;
import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.exception.MatricesIncompatibleForOperationException;
import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.matrixFactory.IntegerMatrixFactoryImpl;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class IntegerMatrixTest {

	@Test
	void testSerialAndParallelDotResultsAreSame() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		var matrixFactory = new IntegerMatrixFactoryImpl(IntegerDotFunctions.getInstance());
		var matrix1 = matrixFactory.withRandomValues(200, 784, -50, 50);
		var matrix2 = matrixFactory.withRandomValues(784, 100, -50, 50);

		var concurrentResult = matrix1.parallelDot(matrix2);
		var serialResult = matrix1.parallelDot(matrix2);

		assertEquals(concurrentResult, serialResult);
	}

	@Test
	void testDotProductResults() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		int MATRIX1_ROWS = 100;
		int MATRIX2_ROWS = 100;
		int MATRIX1_COLS = 100;
		int MATRIX2_COLS = 100;

		Matrix<Integer> matrix1 = new Matrix<>(
				IntegerDotFunctions.getInstance(),
				IntStream.rangeClosed(1, MATRIX1_ROWS).mapToObj(i -> Collections.nCopies(MATRIX1_COLS, Integer.valueOf(i))).collect(Collectors.toList())
		);

		Matrix<Integer> matrix2 = new Matrix<>(
				IntegerDotFunctions.getInstance(),
				IntStream.rangeClosed(1, MATRIX2_ROWS)
						.mapToObj(i -> IntStream.rangeClosed(1, MATRIX2_COLS).boxed().collect(Collectors.toList())
						).collect(Collectors.toList())
		);

		List<List<Integer>> resultList = new ArrayList<>();

		for (var i = 1; i <= MATRIX1_ROWS; i++) {
			int finalI = i;
			resultList.add(Stream.iterate(MATRIX1_ROWS * i, n -> n + MATRIX1_ROWS * finalI).limit(MATRIX1_ROWS).collect(Collectors.toList()));
		}

		Matrix<Integer> expectedResultMatrix = new Matrix<>(IntegerDotFunctions.getInstance(), resultList);

		Matrix<Integer> serialResultMatrix = matrix1.dot(matrix2);
		Matrix<Integer> parallelResultMatrix = matrix1.parallelDot(matrix2);

		assertEquals(expectedResultMatrix, serialResultMatrix);
		assertEquals(expectedResultMatrix, parallelResultMatrix);
	}

	@Test
	void testMatrixAddition() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		var matrix1 = new Matrix<>(IntegerDotFunctions.getInstance(), List.of(
				List.of(1, 2, 3),
				List.of(4, 5, 6)
		));

		var matrix2 = new Matrix<>(IntegerDotFunctions.getInstance(), List.of(
				List.of(2, 3, 4),
				List.of(5, 6, 7)
		));

		var expectedMatrix = new Matrix<>(IntegerDotFunctions.getInstance(), List.of(
				List.of(3, 5, 7),
				List.of(9, 11, 13)
		));

		var result = matrix1.combine(matrix2, Integer::sum);
		assertEquals(expectedMatrix, result);
	}
}