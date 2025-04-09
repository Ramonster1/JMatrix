package org.jmatrix.matrix.concurrent.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiFunction;

public class MatrixRowGroupForkJoinCombineTask<T extends Number> extends RecursiveTask<List<List<T>>> {

	private final List<List<T>> matrix1;
	private final List<List<T>> matrix2;
	private final BiFunction<T, T, T> combineFunction;
	private int threshold; // should be 100 - 10,000 computations

	public MatrixRowGroupForkJoinCombineTask(List<List<T>> matrix1, List<List<T>> matrix2, BiFunction<T, T, T> combineFunction, int threshold) {
		this.matrix1 = matrix1;
		this.matrix2 = matrix2;
		this.combineFunction = combineFunction;
		this.threshold = threshold;
	}

	public List<List<T>> combineLists() {
		List<List<T>> newMatrixListGroup = new ArrayList<>(new ArrayList<>());

		for (var rowIterator = 0; rowIterator < matrix1.size(); rowIterator++) {
			List<T> newMatrixRow = new ArrayList<>();
			for (var colIterator = 0; colIterator < matrix1.getFirst().size(); colIterator++) {
				newMatrixRow.add(combineFunction.apply(matrix1.get(rowIterator).get(colIterator), matrix2.get(rowIterator).get(colIterator)));
			}
			newMatrixListGroup.add(newMatrixRow);
		}
		return newMatrixListGroup;
	}

	@Override
	protected List<List<T>> compute() {
		int computationsPerRow = matrix1.getFirst().size();
		int totalComputations = matrix1.size() * computationsPerRow;

		if (totalComputations < threshold) {
			return combineLists();
		}

		int splitIndex = matrix1.size() / 2;
		MatrixRowGroupForkJoinCombineTask<T> leftTask = new MatrixRowGroupForkJoinCombineTask<>(
				matrix1.subList(0, splitIndex),
				matrix2.subList(0, splitIndex),
				this.combineFunction,
				threshold
		);

		MatrixRowGroupForkJoinCombineTask<T> rightTask = new MatrixRowGroupForkJoinCombineTask<>(
				matrix1.subList(splitIndex, matrix1.size()),
				matrix2.subList(splitIndex, matrix2.size()),
				this.combineFunction,
				threshold
		);
		leftTask.fork();
		List<List<T>> newMatrixListGroupRight = rightTask.compute();
		List<List<T>> newMatrixListGroupLeft = leftTask.join();
		newMatrixListGroupLeft.addAll(newMatrixListGroupRight);

		return newMatrixListGroupLeft;
	}
}

/**
 * OLD
 *
 * Benchmark                                          Mode  Cnt    Score     Error  Units
 * MatrixBenchmarks.measureCombine                   thrpt    4   75.289 ±  20.425  ops/s
 * MatrixBenchmarks.measureDot                       thrpt    4   31.068 ±   4.493  ops/s
 * MatrixBenchmarks.measureForkAndJoinCombine10_000  thrpt    4   55.703 ±  44.027  ops/s
 * MatrixBenchmarks.measureForkAndJoinCombine1_000   thrpt    4   62.572 ±  23.684  ops/s
 * MatrixBenchmarks.measureForkAndJoinCombine5_000   thrpt    4   57.923 ±  47.031  ops/s
 * MatrixBenchmarks.measureForkAndJoinCombine7_000   thrpt    4   57.887 ±   5.061  ops/s
 * MatrixBenchmarks.measureParallelCombine           thrpt    4  185.011 ±  50.972  ops/s
 * MatrixBenchmarks.measureTransform                 thrpt    4  188.542 ± 366.257  ops/s
 * MatrixBenchmarks.measureCombine                    avgt    4    0.012 ±   0.007   s/op
 * MatrixBenchmarks.measureDot                        avgt    4    0.035 ±   0.013   s/op
 * MatrixBenchmarks.measureForkAndJoinCombine10_000   avgt    4    0.016 ±   0.011   s/op
 * MatrixBenchmarks.measureForkAndJoinCombine1_000    avgt    4    0.015 ±   0.002   s/op
 * MatrixBenchmarks.measureForkAndJoinCombine5_000    avgt    4    0.016 ±   0.002   s/op
 * MatrixBenchmarks.measureForkAndJoinCombine7_000    avgt    4    0.015 ±   0.001   s/op
 * MatrixBenchmarks.measureParallelCombine            avgt    4    0.005 ±   0.001   s/op
 * MatrixBenchmarks.measureTransform                  avgt    4    0.005 ±   0.003   s/op
 */