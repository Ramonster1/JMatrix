package org.jmatrix.matrix.matrix.calculator;

import org.jmatrix.matrix.concurrent.task.parallel.MatricesTaskDivider;
import org.jmatrix.matrix.concurrent.task.forkJoin.MatricesForkJoinCombineTask;
import org.jmatrix.matrix.concurrent.task.forkJoin.MatricesForkJoinDotTask;
import org.jmatrix.matrix.concurrent.task.parallel.ParallelMatrixCombineTask;
import org.jmatrix.matrix.concurrent.task.parallel.ParallelMatrixDotTask;
import org.jmatrix.matrix.concurrent.task.parallel.dto.MatrixSubtaskItem;
import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.exception.MatricesIncompatibleForOperationException;
import org.jmatrix.matrix.matrix.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;

public abstract class MatricesCalculator<T> {

	public static final String CANNOT_DOT_MATRICES_ERROR_MSG = "Cannot apply dot function with other matrix. Matrices are not of the same type.";
	public static final String CANNOT_COMBINE_MATRICES_ERROR_MSG = "Cannot apply combine function with other matrix. Columns and rows do not match.";

	/**
	 * Combines each element of this matrix, with the matching element in the matrix2 with a combining function.
	 * E.g. an addition function will produce a new matrix that contains the sums each element in both matrices for
	 * each position.
	 *
	 * @param matrix1     the first matrix to combine with
	 * @param matrix2     the second matrix to combine
	 * @param combineFunction the function to apply each element with
	 * @return a new matrix with same dimensions of this matrix and matrix2 with combineFunction applied on each
	 * element
	 * @throws MatricesIncompatibleForOperationException if the two matrices have different dimensions
	 */
	public static <T> Matrix<T> combine(Matrix<T> matrix1, Matrix<T> matrix2, BiFunction<T, T, T> combineFunction) throws MatricesIncompatibleForOperationException, ListsIncompatibleForMatrixException, EmptyMatrixException {
		validateMatricesForCombine(matrix1, matrix2);

		List<List<T>> matrixLists = new ArrayList<>();
		for (var rowIterator = 0; rowIterator < matrix1.getRows(); rowIterator++) {
			List<T> newMatrixRow = new ArrayList<>();
			for (var colIterator = 0; colIterator < matrix1.getColumns(); colIterator++) {
				newMatrixRow.add(combineFunction.apply(matrix1.getMatrixLists().get(rowIterator).get(colIterator), matrix2.getMatrixLists().get(rowIterator).get(colIterator)));
			}
			matrixLists.add(newMatrixRow);
		}
		return new Matrix<>(matrixLists);
	}

	public static <T> Matrix<T> parallelCombine(Matrix<T> matrix1, Matrix<T> matrix2, BiFunction<T, T, T> combineFunction) throws MatricesIncompatibleForOperationException, ListsIncompatibleForMatrixException, EmptyMatrixException {
		return parallelCombine(matrix1, matrix2, combineFunction, Runtime.getRuntime().availableProcessors());
	}

	public static <T> Matrix<T> parallelCombine(Matrix<T> matrix1, Matrix<T> matrix2, BiFunction<T, T, T> combineFunction, int nThreads) throws MatricesIncompatibleForOperationException, ListsIncompatibleForMatrixException, EmptyMatrixException {
		validateMatricesForCombine(matrix1, matrix2);

		MatricesTaskDivider<T> matricesTaskDivider = new MatricesTaskDivider<>(matrix1.getRows(), nThreads);
		List<Callable<MatrixSubtaskItem<T>>> callables = new ArrayList<>();

		for (var i = 0; i < matricesTaskDivider.getTasks(); i++) {
			callables.add(
					new ParallelMatrixCombineTask<>(
							new Matrix<>(matrix1.getMatrixLists().subList(i * matricesTaskDivider.getStep(), i == matricesTaskDivider.getTasks() - 1 ? matrix1.getRows() : (i + 1) * matricesTaskDivider.getStep())),
							new Matrix<>(matrix2.getMatrixLists().subList(i * matricesTaskDivider.getStep(), i == matricesTaskDivider.getTasks() - 1 ? matrix1.getRows() : (i + 1) * matricesTaskDivider.getStep())),
							combineFunction,
							i));
		}

		return matricesTaskDivider.combineMatricesFromTasks(callables);
	}

	public static <T> Matrix<T> forkAndJoinCombine(Matrix<T> matrix1, Matrix<T> matrix2, BiFunction<T, T, T> combineFunction, int computationsPerTaskThreshold) throws MatricesIncompatibleForOperationException, ListsIncompatibleForMatrixException, EmptyMatrixException {
		if (!isSameDimensions(matrix1, matrix2)) {
			throw new MatricesIncompatibleForOperationException(CANNOT_COMBINE_MATRICES_ERROR_MSG);
		}

		try (ForkJoinPool forkJoinPool = ForkJoinPool.commonPool()) {
			MatricesForkJoinCombineTask<T> combineTask = new MatricesForkJoinCombineTask<>(matrix1, matrix2, combineFunction, computationsPerTaskThreshold);
			return forkJoinPool.invoke(combineTask);
		}
	}

	/**
	 * The number of columns of the 1st matrix must equal the number of rows of the 2nd matrix.
	 * The result will have the same number of rows as the 1st matrix, and the same number of columns as the 2nd matrix.
	 * <a href="https://builtin.com/data-science/dot-product-matrix#:~:text=A%20dot%20product%20of%20a,matrix%20and%20a%202x3%20matrix">
	 * Read this article for more information on dot product and matrix multiplication</a>.
	 *
	 * @param matrix1 the first matrix in the dot equation
	 * @param matrix2 the second matrix in the dot equation
	 * @return the dot product of this matrix and the other matrix
	 */
	public static <T> Matrix<T> dot(Matrix<T> matrix1, Matrix<T> matrix2, T zeroVal, BiFunction<T, T, T> multiplyFunction, BiFunction<T, T, T> accumulatorFunction) throws MatricesIncompatibleForOperationException, EmptyMatrixException, ListsIncompatibleForMatrixException {
		validateMatricesForDot(matrix1, matrix2);

		List<List<T>> matrixLists = new ArrayList<>(new ArrayList<>());

		for (var thisMatrixRowIterator = 0; thisMatrixRowIterator < matrix1.getRows(); thisMatrixRowIterator++) {
			List<T> newMatrixRow = new ArrayList<>();

			for (var otherMatrixColIterator = 0; otherMatrixColIterator < matrix2.getColumns(); otherMatrixColIterator++) {
				T res = zeroVal;

				for (var elementIndex = 0; elementIndex < matrix1.getColumns(); elementIndex++) {
					res = accumulatorFunction.apply(
							res,
							multiplyFunction.apply(
									matrix1.getMatrixLists().get(thisMatrixRowIterator).get(elementIndex),
									matrix2.getMatrixLists().get(elementIndex).get(otherMatrixColIterator)));
				}
				newMatrixRow.add(res);
			}
			matrixLists.add(newMatrixRow);
		}

		return new Matrix<>(matrixLists);
	}

	protected static <T> Matrix<T> parallelDot(Matrix<T> matrix1, Matrix<T> matrix2, T zeroVal, BiFunction<T, T, T> multiplyFunction, BiFunction<T, T, T> accumulatorFunction) throws MatricesIncompatibleForOperationException, EmptyMatrixException, ListsIncompatibleForMatrixException {
		return parallelDot(matrix1, matrix2, Runtime.getRuntime().availableProcessors(), zeroVal, multiplyFunction, accumulatorFunction);
	}

	protected static <T> Matrix<T> parallelDot(Matrix<T> matrix1, Matrix<T> matrix2, int nThreads, T zeroVal, BiFunction<T, T, T> multiplyFunction, BiFunction<T, T, T> accumulatorFunction) throws MatricesIncompatibleForOperationException, EmptyMatrixException, ListsIncompatibleForMatrixException {
		validateMatricesForDot(matrix1, matrix2);

		MatricesTaskDivider<T> matricesTaskDivider = new MatricesTaskDivider<>(matrix1.getRows(), nThreads);
		List<Callable<MatrixSubtaskItem<T>>> callables = new ArrayList<>();

		for (var i = 0; i < matricesTaskDivider.getTasks(); i++) {
			callables.add(
					new ParallelMatrixDotTask<T>(
							new Matrix<>(matrix1.getMatrixLists().subList(i * matricesTaskDivider.getStep(), i == matricesTaskDivider.getTasks() - 1 ? matrix1.getRows() : (i + 1) * matricesTaskDivider.getStep())),
							matrix2,
							i,
							zeroVal,
							multiplyFunction,
							accumulatorFunction));
		}

		return matricesTaskDivider.combineMatricesFromTasks(callables);
	}

	public static <T> Matrix<T> forkJoinDot(Matrix<T> matrix1, Matrix<T> matrix2, int computationsPerTaskThreshold, T zeroVal, BiFunction<T, T, T> multiplyFunction, BiFunction<T, T, T> accumulatorFunction) throws MatricesIncompatibleForOperationException, EmptyMatrixException, ListsIncompatibleForMatrixException {
		validateMatricesForDot(matrix1, matrix2);

		try (ForkJoinPool forkJoinPool = ForkJoinPool.commonPool()) {
			MatricesForkJoinDotTask<T> dotTask = new MatricesForkJoinDotTask<>(
					matrix1,
					matrix2,
					computationsPerTaskThreshold,
					zeroVal,
					multiplyFunction,
					accumulatorFunction);

			return forkJoinPool.invoke(dotTask);
		}
	}

	public static <T> boolean isSameDimensions(Matrix<T> matrix1, Matrix<T> matrix2) {
		return matrix1.getRows() == matrix2.getRows() && matrix1.getColumns() == matrix2.getColumns();
	}

	/**
	 * To multiply two matrices, the number of columns of the 1st matrix must equal the number of rows of the 2nd matrix.
	 * Hence, the result matrix depends on the order of the arguments.
	 *
	 * @return true if the dot operation can be performed on matrix1 & matrix2
	 */
	protected static <T> boolean canDot(Matrix<T> matrix1, Matrix<T> matrix2) {
		return matrix1.getColumns() == matrix2.getRows();
	}

	protected static <T> void validateMatricesForDot(Matrix<T> matrix1, Matrix<T> matrix2) throws MatricesIncompatibleForOperationException {
		if (!Matrix.isSameType(matrix1, matrix2)) {
			throw new MatricesIncompatibleForOperationException(CANNOT_DOT_MATRICES_ERROR_MSG);
		}

		if (!canDot(matrix1, matrix2)) {
			throw new MatricesIncompatibleForOperationException(generateIncompatibleMatricesForDotError(matrix1, matrix2));
		}
	}

	protected static <T> void validateMatricesForCombine(Matrix<T> matrix1, Matrix<T> matrix2) throws MatricesIncompatibleForOperationException {
		if (!Matrix.isSameType(matrix1, matrix2)) {
			throw new MatricesIncompatibleForOperationException(CANNOT_DOT_MATRICES_ERROR_MSG);
		}

		if (!Matrix.isSameDimensions(matrix1, matrix2)) {
			throw new MatricesIncompatibleForOperationException(CANNOT_COMBINE_MATRICES_ERROR_MSG);
		}
	}

	protected static <T> String generateIncompatibleMatricesForDotError(Matrix<T> matrix1, Matrix<T> matrix2) {
		return "Cannot create dot product for matrices. Matrices are incompatible for multiplication." +
				"Columns of matrix 1: " + matrix1.getColumns() + " does not match rows of matrix 2: " + matrix2.getRows();
	}
}
