package org.jmatrix.matrix.matrixFactory;

import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BigDecimalMatrixFactory {

	public static Matrix<BigDecimal> withRandomValues(int rows, int columns, BigDecimal minRange, BigDecimal maxRange) throws EmptyMatrixException, ListsIncompatibleForMatrixException {
		List<List<BigDecimal>> matrixList = new ArrayList<>();

		for (var i = 0; i < rows; i++) {
			matrixList.add(createListOfRandomNumbers(minRange, maxRange, columns));
		}

		return new Matrix<>(matrixList);
	}

	private static List<BigDecimal> createListOfRandomNumbers(BigDecimal min, BigDecimal max, int length) {
		return Stream.generate(() -> generateRandomBigDecimalFromRange(min, max)).limit(length).collect(Collectors.toList());
	}

	private static BigDecimal generateRandomBigDecimalFromRange(BigDecimal min, BigDecimal max) {
		return min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
	}
}
