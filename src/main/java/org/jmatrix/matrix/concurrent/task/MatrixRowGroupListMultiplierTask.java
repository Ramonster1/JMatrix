package org.jmatrix.matrix.concurrent.task;

import org.jmatrix.matrix.matrix.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;

public class MatrixRowGroupListMultiplierTask<T extends Number> implements Callable<List<List<T>>> {

	private final Matrix<T> matrix1;
	private final Matrix<T> matrix2;
	private final int indexStart;
	private final int indexEnd;
	private final BiFunction<T, T, T> multiplyFunction;
	private final T zeroVal;
	private final BiFunction<T, T, T> accumulatorFunction;

	public MatrixRowGroupListMultiplierTask(Matrix<T> matrix1, Matrix<T> matrix2, int indexStart, int indexEnd, BiFunction<T, T, T> multiplyFunction, T zeroVal, BiFunction<T, T, T> accumulatorFunction) {
		this.matrix1 = matrix1;
		this.matrix2 = matrix2;
		this.indexStart = indexStart;
		this.indexEnd = indexEnd;
		this.multiplyFunction = multiplyFunction;
		this.zeroVal = zeroVal;
		this.accumulatorFunction = accumulatorFunction;
	}

	@Override
	public List<List<T>> call() {
		List<List<T>> newMatrixListGroup = new ArrayList<>(new ArrayList<>());

		for (var matrix1RowIterator = indexStart; matrix1RowIterator < indexEnd; matrix1RowIterator++) {
			List<T> newMatrixRow = new ArrayList<>();
			for (var matrix2ColIterator = 0; matrix2ColIterator < matrix2.getColumns(); matrix2ColIterator++) {
				T res = zeroVal;

				for (var elementIndex = 0; elementIndex < matrix1.getColumns(); elementIndex++) {
					res = accumulatorFunction.apply(
							res,
							multiplyFunction.apply(
									matrix1.getMatrixLists().get(matrix1RowIterator).get(elementIndex),
									matrix2.getMatrixLists().get(elementIndex).get(matrix2ColIterator)));
				}
				newMatrixRow.add(res);
			}
			newMatrixListGroup.add(newMatrixRow);
		}

		return newMatrixListGroup;
	}
}
