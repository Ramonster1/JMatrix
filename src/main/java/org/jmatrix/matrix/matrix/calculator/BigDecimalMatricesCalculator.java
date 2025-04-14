package org.jmatrix.matrix.matrix.calculator;

import org.jmatrix.matrix.matrix.Matrix;

import java.math.BigDecimal;
import java.util.function.BiFunction;

public class BigDecimalMatricesCalculator extends MatricesCalculator<BigDecimal> {

	public static Matrix<BigDecimal> dot(Matrix<BigDecimal> matrix1, Matrix<BigDecimal> matrix2) {
		return MatricesCalculator.dot(matrix1, matrix2, getZeroVal(), getMultiplyFunction(), getAccumulatorFunction());
	}

	public static Matrix<BigDecimal> parallelDot(Matrix<BigDecimal> matrix1, Matrix<BigDecimal> matrix2) {
		return MatricesCalculator.parallelDot(matrix1, matrix2, getZeroVal(), getMultiplyFunction(), getAccumulatorFunction());
	}

	public static Matrix<BigDecimal> parallelDot(Matrix<BigDecimal> matrix1, Matrix<BigDecimal> matrix2, int nThreads) {
		return MatricesCalculator.parallelDot(matrix1, matrix2, nThreads, getZeroVal(), getMultiplyFunction(), getAccumulatorFunction());
	}

	public static Matrix<BigDecimal> forkJoinDot(Matrix<BigDecimal> matrix1, Matrix<BigDecimal> matrix2, int threshold) {
		return MatricesCalculator.forkJoinDot(matrix1, matrix2, threshold, getZeroVal(), getMultiplyFunction(), getAccumulatorFunction());
	}

	public static BiFunction<BigDecimal, BigDecimal, BigDecimal> getMultiplyFunction() {
		return BigDecimal::multiply;
	}

	public static BigDecimal getZeroVal() {
		return BigDecimal.ZERO;
	}

	public static BiFunction<BigDecimal, BigDecimal, BigDecimal> getAccumulatorFunction() {
		return BigDecimal::add;
	}
}
