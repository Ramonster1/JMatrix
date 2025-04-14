package org.jmatrix.matrix.concurrent.task.parallel.dto;

import org.jmatrix.matrix.matrix.Matrix;

public record MatrixSubtaskItem<T>(Matrix<T> matrix, int taskNo) implements Comparable<MatrixSubtaskItem<T>> {

	@Override
	public int compareTo(MatrixSubtaskItem<T> o) {
		return Integer.compare(taskNo, o.taskNo);
	}
}
