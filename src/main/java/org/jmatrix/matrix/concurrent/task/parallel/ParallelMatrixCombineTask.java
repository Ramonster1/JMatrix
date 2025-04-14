package org.jmatrix.matrix.concurrent.task.parallel;

import org.jmatrix.matrix.concurrent.task.parallel.dto.MatrixSubtaskItem;
import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.matrix.calculator.MatricesCalculator;

import java.util.concurrent.Callable;
import java.util.function.BiFunction;

public class ParallelMatrixCombineTask<T> implements Callable<MatrixSubtaskItem<T>> {

	private final Matrix<T> matrix1;
	private final Matrix<T> matrix2;
	private final BiFunction<T, T, T> combineFunction;
	private final int taskNo;

	public ParallelMatrixCombineTask(Matrix<T> matrix1, Matrix<T> matrix2, BiFunction<T, T, T> combineFunction, int taskNo) {
		this.matrix1 = matrix1;
		this.matrix2 = matrix2;
		this.combineFunction = combineFunction;
		this.taskNo = taskNo;
	}

	@Override
	public MatrixSubtaskItem<T> call() {
		return new MatrixSubtaskItem<>(MatricesCalculator.combine(matrix1, matrix2, combineFunction), taskNo);
	}
}
