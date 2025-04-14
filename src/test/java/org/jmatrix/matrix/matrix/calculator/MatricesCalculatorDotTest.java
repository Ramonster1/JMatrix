package org.jmatrix.matrix.matrix.calculator;

import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.exception.MatricesIncompatibleForOperationException;
import org.jmatrix.matrix.matrix.Matrix;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MatricesCalculatorDotTest {

	@Test
	public void testDot_validMultiplication() throws EmptyMatrixException, ListsIncompatibleForMatrixException, MatricesIncompatibleForOperationException {
		Matrix<Integer> matrix1 = new Matrix<>(List.of(
				List.of(1, 2),
				List.of(3, 4)
		));

		Matrix<Integer> matrix2 = new Matrix<>(List.of(
				List.of(5, 6),
				List.of(7, 8)
		));

		Matrix<Integer> result = IntegerMatricesCalculator.dot(matrix1, matrix2);

		Matrix<Integer> expected = new Matrix<>(List.of(
				List.of(19, 22),
				List.of(43, 50)
		));

		assertEquals(expected, result);
	}

	@Test
	public void testDot_zeroValMatrix() throws EmptyMatrixException, ListsIncompatibleForMatrixException, MatricesIncompatibleForOperationException {
		Matrix<Integer> matrix1 = new Matrix<>(List.of(
				List.of(0, 0),
				List.of(0, 0)
		));

		Matrix<Integer> matrix2 = new Matrix<>(List.of(
				List.of(1, 2),
				List.of(3, 4)
		));

		Matrix<Integer> result = IntegerMatricesCalculator.dot(matrix1, matrix2);

		Matrix<Integer> expected = new Matrix<>(List.of(
				List.of(0, 0),
				List.of(0, 0)
		));

		assertEquals(expected, result);
	}

	@Test
	public void testDot_edgeCaseSingleValue() throws EmptyMatrixException, ListsIncompatibleForMatrixException, MatricesIncompatibleForOperationException {
		Matrix<BigDecimal> matrix1 = new Matrix<>(List.of(
				List.of(BigDecimal.valueOf(5))
		));

		Matrix<BigDecimal> matrix2 = new Matrix<>(List.of(
				List.of(BigDecimal.valueOf(10))
		));

		Matrix<BigDecimal> result = BigDecimalMatricesCalculator.dot(matrix1, matrix2);

		Matrix<BigDecimal> expected = new Matrix<>(List.of(
				List.of(BigDecimal.valueOf(50))
		));

		assertEquals(expected, result);
	}

	@Test
	public void testDot_incompatibleMatrices_throwsMatricesIncompatibleForOperationException() throws EmptyMatrixException, ListsIncompatibleForMatrixException {
		Matrix<Integer> matrix1 = new Matrix<>(List.of(
				List.of(1, 2, 3),
				List.of(4, 5, 6)
		));

		Matrix<Integer> matrix2 = new Matrix<>(List.of(
				List.of(7, 8)
		));

		assertThrows(MatricesIncompatibleForOperationException.class, () ->
				IntegerMatricesCalculator.dot(matrix1, matrix2));
	}
}
