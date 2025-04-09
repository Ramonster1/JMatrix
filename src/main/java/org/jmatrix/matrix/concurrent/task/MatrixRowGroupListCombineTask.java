package org.jmatrix.matrix.concurrent.task;

import org.jmatrix.matrix.matrix.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;

public class MatrixRowGroupListCombineTask<T extends Number> implements Callable<List<List<T>>> {

	private final Matrix<T> matrix1;
	private final Matrix<T> matrix2;
	private final int indexStart;
	private final int indexEnd;
	private final BiFunction<T, T, T> combineFunction;

	public MatrixRowGroupListCombineTask(Matrix<T> matrix1, Matrix<T> matrix2, int indexStart, int indexEnd, BiFunction<T, T, T> combineFunction) {
		this.matrix1 = matrix1;
		this.matrix2 = matrix2;
		this.indexStart = indexStart;
		this.indexEnd = indexEnd;
		this.combineFunction = combineFunction;
	}

	@Override
	public List<List<T>> call() {
		List<List<T>> newMatrixListGroup = new ArrayList<>(new ArrayList<>());

		for (var rowIterator = indexStart; rowIterator < indexEnd; rowIterator++) {
			List<T> newMatrixRow = new ArrayList<>();
			for (var colIterator = 0; colIterator < matrix1.getColumns(); colIterator++) {
				newMatrixRow.add(combineFunction.apply(matrix1.getMatrixLists().get(rowIterator).get(colIterator), matrix2.getMatrixLists().get(rowIterator).get(colIterator)));
			}
			newMatrixListGroup.add(newMatrixRow);
		}
		return newMatrixListGroup;
	}
}
