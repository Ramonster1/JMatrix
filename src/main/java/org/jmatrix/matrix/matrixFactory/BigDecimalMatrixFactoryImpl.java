package org.jmatrix.matrix.matrixFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmatrix.matrix.dot.DotHelperFunctions;
import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BigDecimalMatrixFactoryImpl extends MatrixFactory<BigDecimal> {
	protected static final Logger logger = LogManager.getLogger(BigDecimalMatrixFactoryImpl.class);

	public BigDecimalMatrixFactoryImpl(DotHelperFunctions<BigDecimal> dotHelperFunctions) {
		super(dotHelperFunctions);
	}

	@Override
	public Matrix<BigDecimal> withRandomValues(int rows, int columns, BigDecimal minRange, BigDecimal maxRange) throws EmptyMatrixException, ListsIncompatibleForMatrixException {
		logger.info("Creating new matrix with {} rows and {} columns. Minimum range is {}  and max range is {}.", rows, columns, minRange, maxRange);

		List<List<BigDecimal>> matrixList = new ArrayList<>();

		for (var i = 0; i < rows; i++) {
			matrixList.add(createListOfRandomNumbers(minRange, maxRange, columns));
		}

		return new Matrix<>(this.dotHelperFunctions, matrixList);
	}

	private static List<BigDecimal> createListOfRandomNumbers(BigDecimal min, BigDecimal max, int length) {
		return Stream.generate(() -> generateRandomBigDecimalFromRange(min, max)).limit(length).collect(Collectors.toList());
	}

	private static BigDecimal generateRandomBigDecimalFromRange(BigDecimal min, BigDecimal max) {
		return min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
	}
}
