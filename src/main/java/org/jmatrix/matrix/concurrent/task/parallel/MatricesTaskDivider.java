package org.jmatrix.matrix.concurrent.task.parallel;

import org.jmatrix.matrix.concurrent.task.parallel.dto.MatrixSubtaskItem;
import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.matrix.Matrix;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MatricesTaskDivider<T> {

	final int tasks;
	final int step;
	public MatricesTaskDivider(int rows, int nThreads) {
		if (rows <= nThreads) {
			step = 1;
			tasks = rows;
		} else {
			step = rows / nThreads;
			tasks = nThreads;
		}
	}

	public Matrix<T> combineMatricesFromTasks(List<Callable<MatrixSubtaskItem<T>>> callables) throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		List<Matrix<T>> orderedMatrixSubtaskItems;
		try (var executorService = Executors.newFixedThreadPool(tasks)) {
			var futures = executorService.invokeAll(callables);

			orderedMatrixSubtaskItems = futures.stream().parallel().map(future -> {
				try {
					return future.get();
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}
			}).sorted().map(MatrixSubtaskItem::matrix).toList();

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		return mergeMatrices(orderedMatrixSubtaskItems);
	}

	public int getTasks() {
		return tasks;
	}

	public int getStep() {
		return step;
	}

	private Matrix<T> mergeMatrices(List<Matrix<T>> orderedMatrixSubtaskItems) {
		Matrix<T> resultMatrix = orderedMatrixSubtaskItems.getFirst();
		for (var i = 1; i < orderedMatrixSubtaskItems.size(); i++) {
			resultMatrix.appendMatrix(orderedMatrixSubtaskItems.get(i));
		}
		return resultMatrix;
	}

}