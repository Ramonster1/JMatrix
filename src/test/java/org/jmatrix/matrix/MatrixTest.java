package org.jmatrix.matrix;

import org.jmatrix.matrix.dot.BigDecimalDotFunctions;
import org.jmatrix.matrix.dot.IntegerDotFunctions;
import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.exception.MatricesIncompatibleForOperationException;
import org.jmatrix.matrix.matrix.Matrix;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MatrixTest {

	@Test
	void testRandomValueConstructor() {
		var matrix = new Matrix<>(BigDecimalDotFunctions.getInstance(),
				10, 15, BigDecimal.valueOf(0.03), BigDecimal.valueOf(0.07),
				(min, max) -> min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min))));

		assertEquals(10, matrix.getRows());
		assertEquals(10, matrix.getMatrixLists().size());

		assertEquals(15, matrix.getColumns());
		assertTrue(matrix.getMatrixLists().stream().allMatch(list -> list.size() == 15),
				"Unexpected column size in matrix");

		assertTrue(matrix.getMatrixLists().stream().allMatch(
						list -> list.stream().allMatch(
								x -> x.compareTo(BigDecimal.valueOf(0.03)) >= 0 && x.compareTo(BigDecimal.valueOf(0.07)) <= 0)),
				"Matrix elements are not within min and max range");
	}

	@Test
	void testListsConstructor() throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		var row1 = List.of(BigDecimal.valueOf(7), BigDecimal.valueOf(8));
		var row2 = List.of(BigDecimal.valueOf(9), BigDecimal.valueOf(10));
		var row3 = List.of(BigDecimal.valueOf(11), BigDecimal.valueOf(12));

		var matrix = new Matrix<>(BigDecimalDotFunctions.getInstance(), List.of(row1, row2, row3));
		assertEquals(row1, matrix.getMatrixLists().getFirst());
		assertEquals(row2, matrix.getMatrixLists().get(1));
		assertEquals(row3, matrix.getMatrixLists().getLast());
	}

	@Test
	void testListsConstructorThrowsExceptions() {
		List<List<BigDecimal>> emptyAndNullList = new ArrayList<>();
		emptyAndNullList.add(List.of());
		emptyAndNullList.add(List.of());
		emptyAndNullList.add(null);

		assertThrows(EmptyMatrixException.class, () -> new Matrix<>(BigDecimalDotFunctions.getInstance(), emptyAndNullList));
	}

	@Test
	void testComputeDotWhenMatricesAreUnmultipliable() throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		var matrix1 = new Matrix<>(BigDecimalDotFunctions.getInstance(), List.of(
				List.of(BigDecimal.valueOf(7), BigDecimal.valueOf(8)),
				List.of(BigDecimal.valueOf(9), BigDecimal.valueOf(10)),
				List.of(BigDecimal.valueOf(11), BigDecimal.valueOf(12))));

		var matrix2 = new Matrix<>(BigDecimalDotFunctions.getInstance(), List.of(
				List.of(BigDecimal.valueOf(7), BigDecimal.valueOf(8)),
				List.of(BigDecimal.valueOf(10), BigDecimal.valueOf(12)),
				List.of(BigDecimal.valueOf(14), BigDecimal.valueOf(16))));

		MatricesIncompatibleForOperationException exception1 = assertThrows(MatricesIncompatibleForOperationException.class, () -> matrix1.dot(matrix2));
		MatricesIncompatibleForOperationException exception2 = assertThrows(MatricesIncompatibleForOperationException.class, () -> matrix2.dot(matrix1));

		String expectedMessage = "Cannot create dot product for matrices. Matrices are incompatible for multiplication.";
		assertEquals(expectedMessage, exception1.getMessage());
		assertEquals(expectedMessage, exception2.getMessage());
	}

	@Test
	void testMatricesIncompatibleForOperationExceptionThrown_WhenMatricesAreIncompatible() throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		var matrix1 = new Matrix<>(BigDecimalDotFunctions.getInstance(), List.of(
				List.of(BigDecimal.valueOf(7), BigDecimal.valueOf(8)),
				List.of(BigDecimal.valueOf(9), BigDecimal.valueOf(10)),
				List.of(BigDecimal.valueOf(11), BigDecimal.valueOf(12))));

		var matrix2 = new Matrix<>(BigDecimalDotFunctions.getInstance(), List.of(
				List.of(BigDecimal.valueOf(7), BigDecimal.valueOf(8)),
				List.of(BigDecimal.valueOf(14), BigDecimal.valueOf(16))));

		MatricesIncompatibleForOperationException e =
				assertThrows(MatricesIncompatibleForOperationException.class, () -> {
					matrix1.combine(matrix2, BigDecimal::add);
				});

		assertEquals("Cannot apply combine function with other matrix. Columns and rows do not match.",
				e.getMessage());
	}

	@Test
	void testResultsWhenApplyingFunctionToAllValuesInMatrix() throws EmptyMatrixException, ListsIncompatibleForMatrixException {
		Matrix<Integer> matrix = new Matrix<>(IntegerDotFunctions.getInstance(), List.of(
				List.of(6, 1),
				List.of(4, -9),
				List.of(24, 8)
		));

		Matrix<Integer> expectedMatrix = new Matrix<>(IntegerDotFunctions.getInstance(), List.of(
				List.of(18, 3),
				List.of(12, -27),
				List.of(72, 24)
		));

		Matrix<Integer> resultMatrix = matrix.transform(x -> x * 3);

		assertEquals(expectedMatrix.getRows(), resultMatrix.getRows(), "Matrix rows should be equal");
		assertEquals(expectedMatrix.getColumns(), resultMatrix.getColumns(), "Matrix rows should be equal");

		int colCount = resultMatrix.getColumns();

		for (var rowIterator = 0; rowIterator < resultMatrix.getRows(); rowIterator++) {
			for (var i = 0; i < colCount; i++) {
				assertEquals(expectedMatrix.getMatrixLists().get(rowIterator).get(i), resultMatrix.getMatrixLists().get(rowIterator).get(i));
			}
		}
	}

	@Test
	void testMatrixTransposition() throws EmptyMatrixException, ListsIncompatibleForMatrixException {
		Matrix<Integer> matrix = new Matrix<>(IntegerDotFunctions.getInstance(), List.of(
				List.of(6, 4, 24),
				List.of(1, -9, 8)
		));

		Matrix<Integer> expectedMatrix = new Matrix<>(IntegerDotFunctions.getInstance(), List.of(
				List.of(6, 1),
				List.of(4, -9),
				List.of(24, 8)
		));

		Matrix<Integer> resultMatrix = matrix.transpose();

		assertEquals(resultMatrix.getMatrixLists().getFirst(), expectedMatrix.getMatrixLists().getFirst());
		assertEquals(resultMatrix.getMatrixLists().get(1), expectedMatrix.getMatrixLists().get(1));
		assertEquals(resultMatrix.getMatrixLists().get(2), expectedMatrix.getMatrixLists().get(2));
	}

	@Test
	void testIsConditionTrueForEachElement() throws EmptyMatrixException, ListsIncompatibleForMatrixException {
		var matrix = new Matrix<>(IntegerDotFunctions.getInstance(), List.of(
				List.of(26, 68),
				List.of(74, 26),
				List.of(50, 36)));

		assertTrue(matrix.isConditionTrueForEachElement(x -> x > 25 && x < 75));
		assertFalse(matrix.isConditionTrueForEachElement(x -> x > 30 && x < 70));
	}

	@Test
	void testEquals() throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		var matrix1 = new Matrix<>(BigDecimalDotFunctions.getInstance(), List.of(
				List.of(BigDecimal.ONE, BigDecimal.valueOf(2), BigDecimal.valueOf(3)),
				List.of(BigDecimal.valueOf(4), BigDecimal.valueOf(5), BigDecimal.valueOf(6))
		));

		var matrix2 = new Matrix<>(BigDecimalDotFunctions.getInstance(), (List.of(
				List.of(BigDecimal.valueOf(7), BigDecimal.valueOf(8)),
				List.of(BigDecimal.valueOf(9), BigDecimal.valueOf(10)),
				List.of(BigDecimal.valueOf(11), BigDecimal.valueOf(12))
		)));

		var matrix3 = new Matrix<>(BigDecimalDotFunctions.getInstance(), List.of(
				List.of(BigDecimal.ONE, BigDecimal.valueOf(2), BigDecimal.valueOf(3)),
				List.of(BigDecimal.valueOf(4), BigDecimal.valueOf(5), BigDecimal.valueOf(6))
		));

		assertNotEquals(matrix1, matrix2);
		assertEquals(matrix1, matrix3);
	}
}
