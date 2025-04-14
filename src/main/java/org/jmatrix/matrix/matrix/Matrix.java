package org.jmatrix.matrix.matrix;

import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * The class {@code Matrix} represents a generic, two-dimensional matrix structure. A matrix is essentially a list of lists.
 * <p>
 * This class uses generics so that matrices with different types can be easily used with very little code.
 *
 * @param <T> the type of elements in the matrix
 */
public class Matrix<T> {
	private final List<List<T>> matrixLists;
	private int rows;
	private int columns;

	public Matrix(List<List<T>> matrixList) throws EmptyMatrixException, ListsIncompatibleForMatrixException {
		if (isEmpty(matrixList)) {
			throw new EmptyMatrixException("Matrix List is empty or null");
		}

		if (!matrixList.stream().allMatch(list -> list.size() == matrixList.getFirst().size())) {
			throw new ListsIncompatibleForMatrixException("Matrix rows have different number of columns");
		}

		// Copy the provided matrixList into a mutable list.
		// The idea here is that GC can be limited when appending matrices.
		this.matrixLists = new ArrayList<>();
		for (var row : matrixList) {
			this.matrixLists.add(new ArrayList<>(row));
		}

		this.rows = matrixList.size();
		this.columns = matrixList.getFirst().size();
	}

	public Matrix(int rows, int columns, T minRange, T maxRange, BiFunction<T, T, T> createRandomValuesFunction) {
		this.matrixLists = initializeRandomMatrix(rows, columns, minRange, maxRange, createRandomValuesFunction);
		this.rows = rows;
		this.columns = columns;
	}

	/**
	 * Appends the rows of the specified matrix to the current matrix. The number of columns
	 * in the specified matrix must match the number of columns in the current matrix to
	 * maintain matrix integrity.
	 * <p>
	 * Why not append the two matrices and create a new matrix object?
	 * Essentially, a matrix is a pointer to a list of lists. Creating a new matrix that points to
	 * the updated list shouldn't have much of a GC impact since only the pointer is lost. However, since
	 * the List interface provides the addAll() method which updates the original list, it makes sense to
	 * follow the same pattern and update the original matrix. Plus, it makes code that uses this method a
	 * little neater. However, this method, plus supporting methods does bloat this class a little.
	 *
	 * @param matrix The matrix whose rows will be appended to the current matrix. It should
	 *               have the same number of columns as the current matrix.
	 */
	public void appendMatrix(Matrix<T> matrix) {
		if (!isSameType(this, matrix) || !isSameColumnCount(this, matrix)) {
			throw new ListsIncompatibleForMatrixException("Cannot append matrix with other matrix. Matrices are not of the same type or have different dimensions.");
		}

		this.matrixLists.addAll(matrix.getMatrixLists());
		this.rows = matrixLists.size();
		this.columns = matrixLists.getFirst().size();
	}

	public static <T> boolean isSameColumnCount(Matrix<T> matrix1, Matrix<T> matrix2) {
		return matrix1.getColumns() == matrix2.getColumns();
	}

	public static <T> boolean isSameDimensions(Matrix<T> matrix1, Matrix<T> matrix2) {
		return matrix1.getRows() == matrix2.getRows() && isSameColumnCount(matrix1, matrix2);
	}

	public static <T> boolean isSameType(Matrix<T> matrix1, Matrix<T> matrix2) {
		return matrix1.getClazz() == matrix2.getClazz();
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

		return null;
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
