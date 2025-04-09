package org.jmatrix.matrix.concurrent;

import org.jmatrix.matrix.dot.DotHelperFunctions;
import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.matrix.Matrix;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MatrixRowGroupTaskDivider<T extends Number> {

	final int tasks;
	final int step;
	public MatrixRowGroupTaskDivider(int rows, int nThreads) {
		if (rows <= nThreads) {
			step = 1;
			tasks = rows;
		} else {
			step = rows / nThreads;
			tasks = nThreads;
		}
	}

	public Matrix<T> combineTasksToLists(Matrix<T> matrix1, Matrix<T> matrix2, DotHelperFunctions<T> dotHelperFunctions, List<Callable<List<List<T>>>> callables) throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		List<List<T>> matrixLists;
		try (var executorService = Executors.newFixedThreadPool(tasks)) {
			var futures = executorService.invokeAll(callables);

			matrixLists = futures.stream().parallel().map(future -> {
				try {
					return future.get();
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}
			}).flatMap(Collection::parallelStream).toList();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return new Matrix<>(dotHelperFunctions, matrixLists);
	}

	public int getTasks() {
		return tasks;
	}

	public int getStep() {
		return step;
	}
}