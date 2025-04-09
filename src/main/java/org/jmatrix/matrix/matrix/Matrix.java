package org.jmatrix.matrix.matrix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmatrix.matrix.concurrent.MatrixRowGroupTaskDivider;
import org.jmatrix.matrix.concurrent.task.MatrixRowGroupForkJoinCombineTask;
import org.jmatrix.matrix.concurrent.task.MatrixRowGroupForkJoinDotTask;
import org.jmatrix.matrix.concurrent.task.MatrixRowGroupListCombineTask;
import org.jmatrix.matrix.concurrent.task.MatrixRowGroupListMultiplierTask;
import org.jmatrix.matrix.dot.DotHelperFunctions;
import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.exception.MatricesIncompatibleForOperationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * The class {@code Matrix} represents a matrix. A matrix is essentially a list of lists.
 * This class contains the default implementation of the necessary behavior to train a neural network e.g. dot product,
 * transpose etc.
 * This class uses generics so that matrices with different Number types can be easily used with very little code. One
 * drawback is the need to pass DotHelperFunctions to the constructor of the matrix since different Number classes
 * use different methods to perform various maths operations which this class needs to know to perform the matrix dot
 * operation. This may be updated in future releases.
 *
 * @param <T> The Type of the Number of each element in the matrix.
 */
public class Matrix<T extends Number> {

	protected static final Logger logger = LogManager.getLogger(Matrix.class);

	private final DotHelperFunctions<T> dotHelperFunctions;
	private final int rows;
	private final int columns;
	private final List<List<T>> matrixLists;

	public Matrix(DotHelperFunctions<T> helperFunctions, List<List<T>> matrixList) throws EmptyMatrixException, ListsIncompatibleForMatrixException {
		this.dotHelperFunctions = helperFunctions;
		if (isEmpty(matrixList)) {
			throw new EmptyMatrixException("Matrix List is empty or null");
		}

		if (!matrixList.stream().allMatch(list -> list.size() == matrixList.getFirst().size())) {
			throw new ListsIncompatibleForMatrixException("Matrix rows have different number of columns");
		}

		this.matrixLists = matrixList;
		this.rows = matrixList.size();
		this.columns = matrixList.getFirst().size();
	}

	public Matrix(DotHelperFunctions<T> helperFunctions, int rows, int columns, T minRange, T maxRange, BiFunction<T, T, T> createRandomValuesFunction) {
		this.dotHelperFunctions = helperFunctions;
		List<List<T>> matrix = new ArrayList<>();
		for (var i = 0; i < rows; i++) {
			List<T> matrixRow = new ArrayList<>();
			for (var j = 0; j < columns; j++) {
				matrixRow.add(createRandomValuesFunction.apply(minRange, maxRange));
			}
			matrix.add(matrixRow);
		}

		this.matrixLists = initializeRandomMatrix(rows, columns, minRange, maxRange, createRandomValuesFunction);
		this.rows = rows;
		this.columns = columns;
	}

	private List<List<T>> initializeRandomMatrix(int rows, int columns, T minRange, T maxRange, BiFunction<T, T, T> createRandomValuesFunction) {
		List<List<T>> matrix = new ArrayList<>();
		for (int i = 0; i < rows; i++) {
			List<T> matrixRow = new ArrayList<>();
			for (int j = 0; j < columns; j++) {
				matrixRow.add(createRandomValuesFunction.apply(minRange, maxRange));
			}
			matrix.add(matrixRow);
		}
		return matrix;
	}

	private boolean isEmpty(List<List<T>> matrixList) {
		return matrixList == null || matrixList.isEmpty() || matrixList.stream().anyMatch((list -> list == null || list.isEmpty()));
	}

	/**
	 * The number of columns of the 1st matrix must equal the number of rows of the 2nd matrix.
	 * The result will have the same number of rows as the 1st matrix, and the same number of columns as the 2nd matrix.
	 * <a href="https://builtin.com/data-science/dot-product-matrix#:~:text=A%20dot%20product%20of%20a,matrix%20and%20a%202x3%20matrix">
	 * Read this article for more information on dot product and matrix multiplication</a>.
	 *
	 * @param otherMatrix to multiply this matrix too
	 * @return the dot product of this matrix and the other matrix
	 */
	public Matrix<T> dot(Matrix<T> otherMatrix) throws MatricesIncompatibleForOperationException, EmptyMatrixException, ListsIncompatibleForMatrixException {
		logger.trace("Doing dot multiplication.");

		if (isUnmultipliableWith(otherMatrix)) {
			throw new MatricesIncompatibleForOperationException("Cannot create dot product for matrices. Matrices are incompatible for multiplication.");
		}

		List<List<T>> matrixLists = new ArrayList<>(new ArrayList<>());

		for (var thisMatrixRowIterator = 0; thisMatrixRowIterator < getRows(); thisMatrixRowIterator++) {
			List<T> newMatrixRow = new ArrayList<>();

			for (var otherMatrixColIterator = 0; otherMatrixColIterator < otherMatrix.getColumns(); otherMatrixColIterator++) {
				T res = this.dotHelperFunctions.getZeroVal();

				for (var elementIndex = 0; elementIndex < getColumns(); elementIndex++) {
					res = this.dotHelperFunctions.getAccumulatorFunction().apply(
							res,
							this.dotHelperFunctions.getMultiplyFunction().apply(
									this.matrixLists.get(thisMatrixRowIterator).get(elementIndex),
									otherMatrix.getMatrixLists().get(elementIndex).get(otherMatrixColIterator)));
				}
				newMatrixRow.add(res);
			}
			matrixLists.add(newMatrixRow);
		}

		return new Matrix<>(this.dotHelperFunctions, matrixLists);
	}

	public Matrix<T> parallelDot(Matrix<T> otherMatrix) throws MatricesIncompatibleForOperationException, EmptyMatrixException, ListsIncompatibleForMatrixException {
		return parallelDot(otherMatrix, Runtime.getRuntime().availableProcessors());
	}

	public Matrix<T> parallelDot(Matrix<T> otherMatrix, int nThreads) throws MatricesIncompatibleForOperationException, EmptyMatrixException, ListsIncompatibleForMatrixException {
		logger.trace("Parallel Dot with {} threads", nThreads);

		if (isUnmultipliableWith(otherMatrix)) {
			throw new MatricesIncompatibleForOperationException("Cannot create dot product for matrices. Matrices are incompatible for multiplication." +
					"This Columns: " + this.getColumns() + " other rows: " + otherMatrix.getRows());
		}

		MatrixRowGroupTaskDivider<T> matrixRowGroupTaskDivider = new MatrixRowGroupTaskDivider<>(this.rows, nThreads);
		List<Callable<List<List<T>>>> callables = new ArrayList<>();

		for (var i = 0; i < matrixRowGroupTaskDivider.getTasks(); i++) {
			callables.add(
					new MatrixRowGroupListMultiplierTask<>(
							this, otherMatrix,
							i * matrixRowGroupTaskDivider.getStep(), i == matrixRowGroupTaskDivider.getTasks() - 1 ? this.getRows() : (i + 1) * matrixRowGroupTaskDivider.getStep(),
							this.dotHelperFunctions.getMultiplyFunction(), this.dotHelperFunctions.getZeroVal(), this.dotHelperFunctions.getAccumulatorFunction()));
		}

		return matrixRowGroupTaskDivider.combineTasksToLists(this, otherMatrix, dotHelperFunctions, callables);
	}

	public Matrix<T> forkJoinDot(Matrix<T> otherMatrix, int threshold) throws MatricesIncompatibleForOperationException, EmptyMatrixException, ListsIncompatibleForMatrixException {
		logger.trace("Calculating dot product with Fork-Join framework with {} threshold.", threshold);

		if (isUnmultipliableWith(otherMatrix)) {
			throw new MatricesIncompatibleForOperationException("Cannot create dot product for matrices. Matrices are incompatible for multiplication." +
					"This Columns: " + this.getColumns() + " other rows: " + otherMatrix.getRows());
		}

		try (ForkJoinPool forkJoinPool = ForkJoinPool.commonPool()) {
			MatrixRowGroupForkJoinDotTask<T> dotTask = new MatrixRowGroupForkJoinDotTask<>(
					this.getMatrixLists(),
					otherMatrix.getMatrixLists(),
					this.dotHelperFunctions,
					threshold);

			List<List<T>> newMatrixLists = forkJoinPool.invoke(dotTask);
			logger.trace("Thread stealing count is {}.", forkJoinPool.getStealCount());

			return new Matrix<>(this.dotHelperFunctions, newMatrixLists);
		}
	}

	/**
	 * Combines each element of this matrix, with the matching element in the otherMatrix with a combining function.
	 * E.g. an addition function will produce a new matrix that contains the sums each element in both matrices for
	 * each position.
	 *
	 * @param otherMatrix     the other matrix to combine with
	 * @param combineFunction the function to apply each element with
	 * @return a new matrix with same dimensions of this matrix and otherMatrix with combineFunction applied on each
	 * element
	 * @throws MatricesIncompatibleForOperationException if the two matrices have different dimensions
	 */
	public Matrix<T> combine(Matrix<T> otherMatrix, BiFunction<T, T, T> combineFunction) throws MatricesIncompatibleForOperationException, ListsIncompatibleForMatrixException, EmptyMatrixException {
		logger.trace("Combining matrices.");

		List<List<T>> matrixLists = new ArrayList<>();
		if (!isSameDimension(otherMatrix)) {
			throw new MatricesIncompatibleForOperationException("Cannot apply combine function with other matrix. Columns and rows do not match.");
		}

		for (var matrixRowIterator = 0; matrixRowIterator < this.getRows(); matrixRowIterator++) {
			List<T> newMatrixRow = new ArrayList<>();
			for (var elementIndex = 0; elementIndex < this.getColumns(); elementIndex++) {
				newMatrixRow.add(combineFunction.apply(this.getMatrixLists().get(matrixRowIterator).get(elementIndex), otherMatrix.getMatrixLists().get(matrixRowIterator).get(elementIndex)));
			}
			matrixLists.add(newMatrixRow);
		}
		return new Matrix<>(this.dotHelperFunctions, matrixLists);
	}

	public Matrix<T> parallelCombine(Matrix<T> otherMatrix, BiFunction<T, T, T> combineFunction) throws MatricesIncompatibleForOperationException, ListsIncompatibleForMatrixException, EmptyMatrixException {
		return parallelCombine(otherMatrix, combineFunction, Runtime.getRuntime().availableProcessors());
	}

	public Matrix<T> parallelCombine(Matrix<T> otherMatrix, BiFunction<T, T, T> combineFunction, int nThreads) throws MatricesIncompatibleForOperationException, ListsIncompatibleForMatrixException, EmptyMatrixException {
		logger.trace("Combining matrices in parallel.");

		if (!isSameDimension(otherMatrix)) {
			throw new MatricesIncompatibleForOperationException("Cannot apply combine function with other matrix. Columns and rows do not match.");
		}

		MatrixRowGroupTaskDivider<T> matrixRowGroupTaskDivider = new MatrixRowGroupTaskDivider<>(this.rows, nThreads);
		List<Callable<List<List<T>>>> callables = new ArrayList<>();
		for (var i = 0; i < matrixRowGroupTaskDivider.getTasks(); i++) {
			callables.add(
					new MatrixRowGroupListCombineTask<>(
							this, otherMatrix,
							i * matrixRowGroupTaskDivider.getStep(), i == matrixRowGroupTaskDivider.getTasks() - 1 ? this.getRows() : (i + 1) * matrixRowGroupTaskDivider.getStep(),
							combineFunction));
		}

		return matrixRowGroupTaskDivider.combineTasksToLists(this, otherMatrix, dotHelperFunctions, callables);
	}

	public Matrix<T> forkAndJoinCombine(Matrix<T> otherMatrix, BiFunction<T, T, T> combineFunction, int threshold) throws MatricesIncompatibleForOperationException, ListsIncompatibleForMatrixException, EmptyMatrixException {
		logger.trace("Combining matrices with Fork-Join framework with {} threshold.", threshold);

		if (!isSameDimension(otherMatrix)) {
			throw new MatricesIncompatibleForOperationException("Cannot apply combine function with other matrix. Columns and rows do not match.");
		}

		try (ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();) {
			MatrixRowGroupForkJoinCombineTask<T> combineTask = new MatrixRowGroupForkJoinCombineTask<>(this.getMatrixLists(), otherMatrix.getMatrixLists(), combineFunction, threshold);

			List<List<T>> newMatrixLists = forkJoinPool.invoke(combineTask);
			logger.trace("Thread stealing count is {}.", forkJoinPool.getStealCount());

			return new Matrix<>(this.dotHelperFunctions, newMatrixLists);
		}
	}

	public Matrix<T> transform(UnaryOperator<T> function) throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		logger.trace("Transforming matrix.");

		List<List<T>> matrixLists = new ArrayList<>();
		for (var row : this.matrixLists) {
			matrixLists.add(row.stream().map(function).toList());
		}

		return new Matrix<>(this.dotHelperFunctions, matrixLists);
	}

	public Matrix<T> transpose() throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		logger.trace("Transposing matrix.");

		List<List<T>> matrixLists = new ArrayList<>();

		for (var colIterator = 0; colIterator < this.getColumns(); colIterator++) {
			List<T> newMatrixRow = new ArrayList<>();
			for (var rowIterator = 0; rowIterator < this.getRows(); rowIterator++) {
				newMatrixRow.add(this.getMatrixLists().get(rowIterator).get(colIterator));
			}

			matrixLists.add(newMatrixRow);
		}
		return new Matrix<>(this.dotHelperFunctions, matrixLists);
	}

	public boolean isConditionTrueForEachElement(Predicate<T> predicate) {
		return this.matrixLists.stream().allMatch(list -> list.stream().allMatch(predicate));
	}

	public boolean isSameDimension(Matrix<T> matrix2) {
		return this.getRows() == matrix2.getRows() && this.getColumns() == matrix2.getColumns();
	}

	/**
	 * To multiply two matrices, the number of columns of the 1st matrix must equal the number of rows of the 2nd matrix.
	 *
	 * @param otherMatrix to multiply
	 * @return dot product of this matrix and otherMatrix
	 */
	public boolean isUnmultipliableWith(Matrix<T> otherMatrix) {
		return this.getColumns() != otherMatrix.getRows();
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public List<List<T>> getMatrixLists() {
		return matrixLists;
	}

	public Class<?> getClazz() {
		if (matrixLists != null && matrixLists.getFirst() != null && !matrixLists.isEmpty() && !matrixLists.getFirst().isEmpty()) {
			return matrixLists.getFirst().getFirst().getClass();
		}

		return Number.class;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Matrix<?> otherMatrix = (Matrix<?>) o;
		return this.rows == otherMatrix.rows && this.columns == otherMatrix.columns && Objects.equals(this.matrixLists, otherMatrix.matrixLists);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.rows, this.columns, this.matrixLists);
	}

	@Override
	public String toString() {
		return "Matrix{" +
				"matrix=" + this.matrixLists +
				'}';
	}

}
