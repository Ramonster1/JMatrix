package org.jmatrix.matrix.matrixFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmatrix.matrix.dot.DotHelperFunctions;
import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IntegerMatrixFactoryImpl extends MatrixFactory<Integer> {
	protected static final Logger logger = LogManager.getLogger(IntegerMatrixFactoryImpl.class);

	public IntegerMatrixFactoryImpl(DotHelperFunctions<Integer> dotHelperFunctions) {
		super(dotHelperFunctions);
	}

	public Matrix<Integer> withRandomValues(int rows, int columns, Integer minRange, Integer maxRange) throws EmptyMatrixException, ListsIncompatibleForMatrixException {
		logger.info("Creating new matrix with {} rows and {} columns. Minimum range is {}  and max range is {}.", rows, columns, minRange, maxRange);

		List<List<Integer>> matrixLists = new ArrayList<>();
		for (var i = 0; i < rows; i++) {
			matrixLists.add(createListOfRandomNumbers(minRange, maxRange, columns));
		}

		return new Matrix<>(this.dotHelperFunctions, matrixLists);
	}


	private static List<Integer> createListOfRandomNumbers(int min, int max, int length) {
		return new Random().ints(length, min, max).boxed().toList();
	}
}
