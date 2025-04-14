package org.jmatrix.matrix.matrix.calculator;

import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.matrix.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public abstract class MatrixCalculator {

	public static <T> Matrix<T> transform(Matrix<T> matrix, UnaryOperator<T> function) throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		List<List<T>> matrixLists = new ArrayList<>();
		for (var row : matrix.getMatrixLists()) {
			matrixLists.add(row.stream().map(function).toList());
		}

		return new Matrix<>(matrixLists);
	}

	public static <T> Matrix<T> transpose(Matrix<T> matrix) throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		List<List<T>> matrixLists = new ArrayList<>();

		for (var colIterator = 0; colIterator < matrix.getColumns(); colIterator++) {
			List<T> newMatrixRow = new ArrayList<>();
			for (var rowIterator = 0; rowIterator < matrix.getRows(); rowIterator++) {
				newMatrixRow.add(matrix.getMatrixLists().get(rowIterator).get(colIterator));
			}

			matrixLists.add(newMatrixRow);
		}
		return new Matrix<>(matrixLists);
	}

	public static <T> boolean isConditionTrueForEachElement(Matrix<T> matrix, Predicate<T> predicate) {
		return matrix.getMatrixLists().stream().allMatch(list -> list.stream().allMatch(predicate));
	}

}
