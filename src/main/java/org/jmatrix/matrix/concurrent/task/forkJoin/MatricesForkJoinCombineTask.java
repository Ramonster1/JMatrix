package org.jmatrix.matrix.concurrent.task.forkJoin;

import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.matrix.calculator.MatricesCalculator;

import java.util.concurrent.RecursiveTask;
import java.util.function.BiFunction;

/**
 * The class {@code MatricesForkJoinCombineTask} is a specialized implementation of the {@link RecursiveTask}
 * for performing parallel combine operations on matrices. This class divides the workload of combining two matrices
 * into smaller, manageable tasks using the Fork/Join framework, which performs a divide-and-conquer approach
 * for parallel processing.
 * <p>
 * The key value here is totalComputationsPerTask. According to Doug Lea's paper on the Fork/Join Framework, this value
 * should be somewhere between 100 - 10,000 computations. In this instance, a computation is the number of combine
 * operations performed on the matrices. So two matrices with 1 row and 100 columns would have 1 * 100 = 100 computations
 * per task.
 *
 * @param <T> the type of the elements in the matrix.
 */
public class MatricesForkJoinCombineTask<T> extends RecursiveTask<Matrix<T>> {

	private final Matrix<T> matrix1;
	private final Matrix<T> matrix2;
	private final BiFunction<T, T, T> combineFunction;
	private final int totalComputationsPerTask;

	public MatricesForkJoinCombineTask(Matrix<T> matrix1, Matrix<T> matrix2, BiFunction<T, T, T> combineFunction, int totalComputationsPerTask) {
		this.matrix1 = matrix1;
		this.matrix2 = matrix2;
		this.combineFunction = combineFunction;
		this.totalComputationsPerTask = totalComputationsPerTask;
	}

	@Override
	protected Matrix<T> compute() {
		int computationsPerRow = matrix1.getColumns();
		int totalComputations = matrix1.getRows() * computationsPerRow;

		if (totalComputations < totalComputationsPerTask || matrix1.getRows() < 2) {
			return MatricesCalculator.combine(this.matrix1, this.matrix2, this.combineFunction);
		}

		int splitIndex = matrix1.getRows() / 2;

		MatricesForkJoinCombineTask<T> leftTask = new MatricesForkJoinCombineTask<>(
				new Matrix<>(matrix1.getMatrixLists().subList(0, splitIndex)),
				new Matrix<>(matrix2.getMatrixLists().subList(0, splitIndex)),
				this.combineFunction,
				totalComputationsPerTask
		);

		MatricesForkJoinCombineTask<T> rightTask = new MatricesForkJoinCombineTask<>(
				new Matrix<>(matrix1.getMatrixLists().subList(splitIndex, matrix1.getRows())),
				new Matrix<>(matrix2.getMatrixLists().subList(splitIndex, matrix2.getRows())),
				this.combineFunction,
				totalComputationsPerTask
		);

		leftTask.fork();
		Matrix<T> matrixRowsRight = rightTask.compute();
		Matrix<T> matrixRowsLeft = leftTask.join();
		matrixRowsLeft.appendMatrix(matrixRowsRight);

		return matrixRowsLeft;
	}
}
