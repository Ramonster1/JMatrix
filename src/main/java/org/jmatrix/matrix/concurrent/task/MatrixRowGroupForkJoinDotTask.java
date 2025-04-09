package org.jmatrix.matrix.concurrent.task;

import org.jmatrix.matrix.dot.DotHelperFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class MatrixRowGroupForkJoinDotTask<T extends Number> extends RecursiveTask<List<List<T>>> {

	private final List<List<T>> matrix1;
	private final List<List<T>> matrix2;
	private final DotHelperFunctions<T> dotHelperFunctions;
	private final int threshold; // should be 100 - 10,000 computations

	public MatrixRowGroupForkJoinDotTask(List<List<T>> matrix1, List<List<T>> matrix2, DotHelperFunctions<T> dotHelperFunctions, int rowsThreshold) {
		this.matrix1 = matrix1;
		this.matrix2 = matrix2;
		this.dotHelperFunctions = dotHelperFunctions;
		this.threshold = rowsThreshold;
	}

	public List<List<T>> multiplyMatrices() {
		List<List<T>> newMatrixListGroup = new ArrayList<>(new ArrayList<>());
		int matrix1Columns = matrix1.getFirst().size();

		for (var matrix1RowIterator = 0; matrix1RowIterator < matrix1.size(); matrix1RowIterator++) {
			List<T> newMatrixRow = new ArrayList<>();

			for (var matrix2ColIterator = 0; matrix2ColIterator < matrix2.getFirst().size(); matrix2ColIterator++) {
				T res = this.dotHelperFunctions.getZeroVal();

				for (var elementIndex = 0; elementIndex < matrix1Columns; elementIndex++) {
					res = this.dotHelperFunctions.getAccumulatorFunction().apply(
							res,
							this.dotHelperFunctions.getMultiplyFunction().apply(
									matrix1.get(matrix1RowIterator).get(elementIndex),
									matrix2.get(elementIndex).get(matrix2ColIterator)));
				}
				newMatrixRow.add(res);
			}
			newMatrixListGroup.add(newMatrixRow);
		}
		return newMatrixListGroup;
	}

	@Override
	protected List<List<T>> compute() {
		int computationsPerRow = matrix1.getFirst().size() * matrix2.size();
		int totalComputations = matrix1.size() * computationsPerRow;

		if (matrix1.size() < threshold) {
			return multiplyMatrices();
		}

		int splitIndex = matrix1.size() / 2;
		MatrixRowGroupForkJoinDotTask<T> leftTask = new MatrixRowGroupForkJoinDotTask<>(
				matrix1.subList(0, splitIndex),
				matrix2,
				this.dotHelperFunctions,
				threshold
		);

		MatrixRowGroupForkJoinDotTask<T> rightTask = new MatrixRowGroupForkJoinDotTask<>(
				matrix1.subList(splitIndex, matrix1.size()),
				matrix2,
				this.dotHelperFunctions,
				threshold
		);
		leftTask.fork();
		List<List<T>> newMatrixListGroupRight = rightTask.compute();
		List<List<T>> newMatrixListGroupLeft = leftTask.join();
		newMatrixListGroupLeft.addAll(newMatrixListGroupRight);

		return newMatrixListGroupLeft;
	}
}
