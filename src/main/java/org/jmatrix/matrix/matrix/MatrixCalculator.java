package org.jmatrix.matrix.matrix;

import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.exception.MatricesIncompatibleForOperationException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public abstract class MatrixCalculator {

// todo dot methods get put into concrete impl

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

}
