package org.jmatrix.matrix.matrixFactory;

import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IntegerMatrixFactory {

	public static Matrix<Integer> withRandomValues(int rows, int columns, Integer minRange, Integer maxRange) throws EmptyMatrixException, ListsIncompatibleForMatrixException {

		List<List<Integer>> matrixLists = new ArrayList<>();
		for (var i = 0; i < rows; i++) {
			matrixLists.add(createListOfRandomNumbers(minRange, maxRange, columns));
		}

		return new Matrix<>(matrixLists);
	}


	private static List<Integer> createListOfRandomNumbers(int min, int max, int length) {
		return new Random().ints(length, min, max).boxed().toList();
	}
}
