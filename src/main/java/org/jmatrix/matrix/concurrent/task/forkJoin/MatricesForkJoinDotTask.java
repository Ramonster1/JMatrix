package org.jmatrix.matrix.concurrent.task.forkJoin;

import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.matrix.calculator.MatricesCalculator;

import java.util.concurrent.RecursiveTask;
import java.util.function.BiFunction;

/**
 * The {@code MatricesForkJoinDotTask} class represents a task for computing the dot product of two matrices
 * using the Fork/Join framework for parallel computation. It extends the {@code RecursiveTask} class with a result
 * type of {@code Matrix<T>}.
 * <p>
 * This task recursively divides the matrix multiplication computation into subtasks until the amount of work
 * is small enough to compute directly, based on a specified threshold.
 * <p>
 * The key value here is computationsPerTaskThreshold. According to Doug Lea's paper on the Fork/Join Framework, this value
 * should be somewhere between 100 - 10,000 computations. In this instance, a computation is the number of mathematical
 * operations performed on the matrices to calculate the dot product. This includes multiplying and summing the entries
 * in each matrix to calculate the result entry for the result matrix.
 * For example, if:
 * - matrix 1 has 2 rows & 3 columns
 * - matrix 2 has 3 rows & 2 columns
 * - it takes 5 computation (3 multiplications and 2 additions) to calculate the value for row 1, column 1 of the result
 * 		matrix.
 * 	- Total number of entries for the result matrix is matrix1 rows * matrix2 columns = 4.
 * 	- So, Total number of computations for this example task is: 5 * 4 = 20.
 * 	This particular example can be found <a href="https://www.mathsisfun.com/algebra/matrix-multiplying.html">here</a>.
 *
 * @param <T> the type of elements in the matrices, determined by the {@code Matrix<T>} type
 */
public class MatricesForkJoinDotTask<T> extends RecursiveTask<Matrix<T>> {

	private final Matrix<T> matrix1;
	private final Matrix<T> matrix2;
	private final int computationsPerTaskThreshold; // should be 100 - 10,000 computations altogether
	private final T zeroVal;
	private final BiFunction<T, T, T> multiplyFunction;
	private final BiFunction<T, T, T> accumulatorFunction;

	public MatricesForkJoinDotTask(Matrix<T> matrix1, Matrix<T> matrix2, int rowsThreshold, T zeroVal, BiFunction<T, T, T> multiplyFunction, BiFunction<T, T, T> accumulatorFunction) {
		this.matrix1 = matrix1;
		this.matrix2 = matrix2;
		this.computationsPerTaskThreshold = rowsThreshold;
		this.multiplyFunction = multiplyFunction;
		this.zeroVal = zeroVal;
		this.accumulatorFunction = accumulatorFunction;
	}

	public Matrix<T> multiplyMatrices() {
		return MatricesCalculator.dot(matrix1, matrix2, zeroVal, multiplyFunction, accumulatorFunction);
	}

	/**
	 * Executes the dot product computation of two matrices using the Fork/Join framework.
	 * If the number of computation to calculate the dot product is below a specified threshold, or there's only 1 row
	 * in the matrix, then it computes the result directly. Otherwise, it splits the computation into two subtasks and
	 * recursively processes each subtask. The computation is split by halving the first matrix by its rows.
	 *
	 * @return A Matrix representing the dot product computation of two matrices, where each
	 * inner list corresponds to a row in the resulting matrix.
	 */
	@Override
	protected Matrix<T> compute() {
		int computationsPerResultMatrixEntry = matrix1.getColumns() + matrix2.getColumns() - 1;
		int totalEntriesInResultMatrix = matrix1.getRows() * matrix2.getColumns();
		int computationsPerTask = computationsPerResultMatrixEntry * totalEntriesInResultMatrix;

		if (computationsPerTask < computationsPerTaskThreshold || matrix1.getRows() < 2) {
			return multiplyMatrices();
		}

		int splitIndex = matrix1.getRows() / 2;
		MatricesForkJoinDotTask<T> leftTask = new MatricesForkJoinDotTask<>(
				new Matrix<>(matrix1.getMatrixLists().subList(0, splitIndex)),
				matrix2,
				computationsPerTaskThreshold,
				zeroVal,
				multiplyFunction,
				accumulatorFunction
		);

		MatricesForkJoinDotTask<T> rightTask = new MatricesForkJoinDotTask<>(
				new Matrix<>(matrix1.getMatrixLists().subList(splitIndex, matrix1.getRows())),
				matrix2,
				computationsPerTaskThreshold,
				zeroVal,
				multiplyFunction,
				accumulatorFunction
		);
		leftTask.fork();
		Matrix<T> newMatrixListGroupRight = rightTask.compute();
		Matrix<T> newMatrixListGroupLeft = leftTask.join();
		newMatrixListGroupLeft.appendMatrix(newMatrixListGroupRight);

		return newMatrixListGroupLeft;
	}
}
