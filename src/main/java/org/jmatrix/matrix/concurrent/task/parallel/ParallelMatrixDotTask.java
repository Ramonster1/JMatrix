package org.jmatrix.matrix.concurrent.task.parallel;

import org.jmatrix.matrix.concurrent.task.parallel.dto.MatrixSubtaskItem;
import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.matrix.calculator.MatricesCalculator;

import java.util.concurrent.Callable;
import java.util.function.BiFunction;

public class ParallelMatrixDotTask<T> implements Callable<MatrixSubtaskItem<T>> {

	private final Matrix<T> matrix1;
	private final Matrix<T> matrix2;
	private final int taskNo;
	private final T zeroVal;
	private final BiFunction<T, T, T> multiplyFunction;
	private final BiFunction<T, T, T> accumulatorFunction;

	public ParallelMatrixDotTask(Matrix<T> matrix1, Matrix<T> matrix2, int taskNo, T zeroVal, BiFunction<T, T, T> multiplyFunction, BiFunction<T, T, T> accumulatorFunction) {
		this.matrix1 = matrix1;
		this.matrix2 = matrix2;
		this.taskNo = taskNo;
		this.multiplyFunction = multiplyFunction;
		this.zeroVal = zeroVal;
		this.accumulatorFunction = accumulatorFunction;
	}

	@Override
	public MatrixSubtaskItem<T> call() {
		return new MatrixSubtaskItem<>(MatricesCalculator.dot(matrix1, matrix2, zeroVal, multiplyFunction, accumulatorFunction), taskNo);
	}
}
