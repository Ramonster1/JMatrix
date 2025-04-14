package org.jmatrix.matrix.matrix.calculator;

import org.jmatrix.matrix.matrix.Matrix;

import java.util.function.BiFunction;

public class IntegerMatricesCalculator extends MatricesCalculator<Integer> {

	public static Matrix<Integer> dot(Matrix<Integer> matrix1, Matrix<Integer> matrix2) {
		return MatricesCalculator.dot(matrix1, matrix2, getZeroVal(), getMultiplyFunction(), getAccumulatorFunction());
	}

	public static Matrix<Integer> parallelDot(Matrix<Integer> matrix1, Matrix<Integer> matrix2) {
		return MatricesCalculator.parallelDot(matrix1, matrix2, getZeroVal(), getMultiplyFunction(), getAccumulatorFunction());
	}

	public static Matrix<Integer> parallelDot(Matrix<Integer> matrix1, Matrix<Integer> matrix2, int nThreads) {
		return MatricesCalculator.parallelDot(matrix1, matrix2, nThreads, getZeroVal(), getMultiplyFunction(), getAccumulatorFunction());
	}

	public static Matrix<Integer> forkJoinDot(Matrix<Integer> matrix1, Matrix<Integer> matrix2, int threshold) {
		return MatricesCalculator.forkJoinDot(matrix1, matrix2, threshold, getZeroVal(), getMultiplyFunction(), getAccumulatorFunction());
	}

	public static BiFunction<Integer, Integer, Integer> getMultiplyFunction() {
		return (x, y) -> (x * y);
	}

	public static Integer getZeroVal() {
		return Integer.valueOf(0);
	}

	public static BiFunction<Integer, Integer, Integer> getAccumulatorFunction() {
		return Integer::sum;
	}
}
