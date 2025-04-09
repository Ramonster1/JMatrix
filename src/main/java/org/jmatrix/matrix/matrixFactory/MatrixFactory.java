package org.jmatrix.matrix.matrixFactory;

import org.jmatrix.matrix.dot.DotHelperFunctions;
import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;


/**
 * This interface defines the basic behavior for creating matrices with random values. The constructor in the Matrix
 * class can also be used to do this, however it requires passing a function to create random values in every
 * constructor call. This can instead be hard-coded in a factory implementation.
 */
public abstract class MatrixFactory<T extends Number> {

	public MatrixFactory(DotHelperFunctions<T> dotHelperFunctions) {
		this.dotHelperFunctions = dotHelperFunctions;
	}

	DotHelperFunctions<T> dotHelperFunctions;

	public abstract Matrix<T> withRandomValues(int rows, int columns, T minRange, T maxRange) throws EmptyMatrixException, ListsIncompatibleForMatrixException;

}
