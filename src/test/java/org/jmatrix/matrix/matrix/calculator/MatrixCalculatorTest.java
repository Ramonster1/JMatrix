package org.jmatrix.matrix.matrix.calculator;

import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.matrix.Matrix;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MatrixCalculatorTest {

	@Test
	void testTransformWithAdditionOperation() throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		// Arrange
		List<List<Integer>> matrixData = List.of(
				List.of(1, 2, 3),
				List.of(4, 5, 6),
				List.of(7, 8, 9)
		);
		Matrix<Integer> matrix = new Matrix<>(matrixData);

		// Unary operation to add 10 to each element
		UnaryOperator<Integer> additionFunction = (element) -> element + 10;

		// Act
		Matrix<Integer> transformedMatrix = MatrixCalculator.transform(matrix, additionFunction);

		// Assert
		List<List<Integer>> expectedData = List.of(
				List.of(11, 12, 13),
				List.of(14, 15, 16),
				List.of(17, 18, 19)
		);
		assertEquals(new Matrix<>(expectedData), transformedMatrix);
	}

	@Test
	void testTransformWithMultiplicationOperation() throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		// Arrange
		List<List<Integer>> matrixData = List.of(
				List.of(1, 2, 3),
				List.of(4, 5, 6),
				List.of(7, 8, 9)
		);
		Matrix<Integer> matrix = new Matrix<>(matrixData);

		// Unary operation to multiply each element by 2
		UnaryOperator<Integer> multiplicationFunction = (element) -> element * -2;

		// Act
		Matrix<Integer> transformedMatrix = MatrixCalculator.transform(matrix, multiplicationFunction);

		// Assert
		List<List<Integer>> expectedData = List.of(
				List.of(-2, -4, -6),
				List.of(-8, -10, -12),
				List.of(-14, -16, -18)
		);
		assertEquals(new Matrix<>(expectedData), transformedMatrix);
	}

	@Test
	void testTransformWithIncompatibleMatrix() {
		// Arrange
		List<List<Integer>> incompatibleData = List.of(
				List.of(1, 2),
				List.of(3, 4, 5)
		);

		// Act and Assert
		assertThrows(ListsIncompatibleForMatrixException.class, () -> new Matrix<>(incompatibleData));
	}
}