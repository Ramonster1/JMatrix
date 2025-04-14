package org.jmatrix.matrix;

import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.matrix.Matrix;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MatrixTest {

	@Test
	void testRandomValueConstructor() {
		var matrix = new Matrix<>(10,
				15,
				BigDecimal.valueOf(0.03),
				BigDecimal.valueOf(0.07),
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

		var matrix = new Matrix<>(List.of(row1, row2, row3));
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

		assertThrows(EmptyMatrixException.class, () -> new Matrix<>(emptyAndNullList));
	}

	@Test
	void testEquals() throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		var matrix1 = new Matrix<>(List.of(
				List.of(BigDecimal.ONE, BigDecimal.valueOf(2), BigDecimal.valueOf(3)),
				List.of(BigDecimal.valueOf(4), BigDecimal.valueOf(5), BigDecimal.valueOf(6))
		));

		var matrix2 = new Matrix<>((List.of(
				List.of(BigDecimal.valueOf(7), BigDecimal.valueOf(8)),
				List.of(BigDecimal.valueOf(9), BigDecimal.valueOf(10)),
				List.of(BigDecimal.valueOf(11), BigDecimal.valueOf(12))
		)));

		var matrix3 = new Matrix<>(List.of(
				List.of(BigDecimal.ONE, BigDecimal.valueOf(2), BigDecimal.valueOf(3)),
				List.of(BigDecimal.valueOf(4), BigDecimal.valueOf(5), BigDecimal.valueOf(6))
		));

		assertNotEquals(matrix1, matrix2);
		assertEquals(matrix1, matrix3);
	}

	@Test
	void testAppendMatrix() {
		var matrix1 = new Matrix<>(List.of(
				List.of(BigDecimal.valueOf(1), BigDecimal.valueOf(2)),
				List.of(BigDecimal.valueOf(3), BigDecimal.valueOf(4))
		));

		var matrix2 = new Matrix<>(List.of(
				List.of(BigDecimal.valueOf(5), BigDecimal.valueOf(6)),
				List.of(BigDecimal.valueOf(7), BigDecimal.valueOf(8))
		));

		var matrix3 = new Matrix<>(List.of(
				List.of(BigDecimal.valueOf(9), BigDecimal.valueOf(10))
		));

		matrix1.appendMatrix(matrix2);
		matrix1.appendMatrix(matrix3);

		assertEquals(5, matrix1.getRows(), "Row count should reflect the appended matrix's row count");
		assertEquals(2, matrix1.getColumns(), "Column count should remain unchanged");
		List<List<BigDecimal>> expectedMatrix = List.of(
				List.of(BigDecimal.valueOf(1), BigDecimal.valueOf(2)),
				List.of(BigDecimal.valueOf(3), BigDecimal.valueOf(4)),
				List.of(BigDecimal.valueOf(5), BigDecimal.valueOf(6)),
				List.of(BigDecimal.valueOf(7), BigDecimal.valueOf(8)),
				List.of(BigDecimal.valueOf(9), BigDecimal.valueOf(10))
		);
		assertEquals(expectedMatrix, matrix1.getMatrixLists(), "Matrix contents do not match after append");
	}

	@Test
	void testCreatingEmptyMatrixThrowsException() {
		assertThrows(EmptyMatrixException.class, () -> new Matrix<BigDecimal>(List.of()),
				"Creating an empty matrix should throw exception");
	}

	@Test
	void testAppendMatrixDifferentColumnSizesThrowsException() {
		var matrix1 = new Matrix<>(List.of(
				List.of(BigDecimal.valueOf(1), BigDecimal.valueOf(2)),
				List.of(BigDecimal.valueOf(3), BigDecimal.valueOf(4))
		));

		var matrix2 = new Matrix<>(List.of(
				List.of(BigDecimal.valueOf(5)),
				List.of(BigDecimal.valueOf(6))
		));

		assertThrows(ListsIncompatibleForMatrixException.class, () -> matrix1.appendMatrix(matrix2),
				"Appending a matrix with different column size should throw exception");
	}
}
