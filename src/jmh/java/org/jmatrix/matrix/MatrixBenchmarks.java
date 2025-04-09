package org.jmatrix.matrix;

import org.jmatrix.matrix.dot.BigDecimalDotFunctions;
import org.jmatrix.matrix.exception.EmptyMatrixException;
import org.jmatrix.matrix.exception.ListsIncompatibleForMatrixException;
import org.jmatrix.matrix.exception.MatricesIncompatibleForOperationException;
import org.jmatrix.matrix.matrix.Matrix;
import org.jmatrix.matrix.matrixFactory.BigDecimalMatrixFactoryImpl;
import org.jmatrix.matrix.matrixFactory.MatrixFactory;
import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;

@State(Scope.Benchmark)
public class MatrixBenchmarks {

	private Matrix<BigDecimal> matrix1;
	private Matrix<BigDecimal> matrix2;
	private Matrix<BigDecimal> matrix3;

	/**
	 * Run ./gradlew jmh
	 */

	// todo create more tests for different sizes, small, medium & large https://www.reddit.com/r/java/comments/la775b/how_are_you_organizing_your_unit_integration_and/

	@Setup(Level.Invocation)
	public void setUp() throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		MatrixFactory<BigDecimal> matrixFactory = new BigDecimalMatrixFactoryImpl(BigDecimalDotFunctions.getInstance());
		matrix1 = matrixFactory.withRandomValues(200, 784, BigDecimal.valueOf(-0.5), BigDecimal.valueOf(0.5));
		matrix2 = matrixFactory.withRandomValues(784, 1, BigDecimal.valueOf(-0.5), BigDecimal.valueOf(0.5));
		matrix3 = matrixFactory.withRandomValues(200, 784, BigDecimal.valueOf(-0.5), BigDecimal.valueOf(0.5));
	}

/*	@Benchmark
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	public Matrix<BigDecimal> withRandomValues() throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		return matrixFactory.withRandomValues(200, 784, BigDecimal.valueOf(-0.5), BigDecimal.valueOf(0.5));
	}*/

	@Benchmark
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	public Matrix<BigDecimal> measureDot() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		return matrix1.dot(matrix2);
	}

	@Benchmark
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	public Matrix<BigDecimal> measureParallelDot() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		return matrix1.parallelDot(matrix2);
	}

	@Benchmark
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	public Matrix<BigDecimal> measureForkJoinDot_10() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		return matrix1.forkJoinDot(matrix2, 10);
	}

	@Benchmark
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	public Matrix<BigDecimal> measureForkJoinDot_20() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		return matrix1.forkJoinDot(matrix2, 20);
	}

	/*@Benchmark
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	public Matrix<BigDecimal> measureForkJoinDot_3_000_000() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		return matrix1.forkJoinDot(matrix2, 3_000_000);
	}
*/
	/*@Benchmark
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	public Matrix<BigDecimal> measureCombine() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		return matrix1.combine(matrix3, BigDecimal::add);
	}

	@Benchmark
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	public Matrix<BigDecimal> measureParallelCombine() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		return matrix1.parallelCombine(matrix3, BigDecimal::add);
	}*/

	/*@Benchmark
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	public Matrix<BigDecimal> measureForkAndJoinCombine500() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		return matrix1.forkAndJoinCombine(matrix3, BigDecimal::add, 500); // StackOverflowError
	}

	@Benchmark
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	public Matrix<BigDecimal> measureForkAndJoinCombine1_000() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		return matrix1.forkAndJoinCombine(matrix3, BigDecimal::add, 1_000);
	}

	@Benchmark
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	public Matrix<BigDecimal> measureForkAndJoinCombine5_000() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		return matrix1.forkAndJoinCombine(matrix3, BigDecimal::add, 5_000);
	}

	@Benchmark
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	public Matrix<BigDecimal> measureForkAndJoinCombine7_000() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		return matrix1.forkAndJoinCombine(matrix3, BigDecimal::add, 7_000);
	}*/

/*	@Benchmark
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	public Matrix<BigDecimal> measureForkAndJoinCombine10_000() throws ListsIncompatibleForMatrixException, EmptyMatrixException, MatricesIncompatibleForOperationException {
		return matrix1.forkAndJoinCombine(matrix3, BigDecimal::add, 10_000);
	}

	@Benchmark
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	public Matrix<BigDecimal> measureTransform() throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		return matrix1.transform(BigDecimal::negate);
	}*/

	/**
	 * https://www.endoflineblog.com/running-a-single-jmh-benchmark-with-gradle
	 * https://jenkov.com/tutorials/java-performance/jmh.html#state-setup-and-teardown
	 * https://blog.morethan.io/jmh-with-gradle-from-easy-to-simple-dc872d57cf7f
	 * https://mihaibojin.medium.com/microbenchmarking-java-code-with-jmh-823465902d0c
	 */

	/*@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public Matrix<BigDecimal> create() throws ListsIncompatibleForMatrixException, EmptyMatrixException {
		MatrixFactory<BigDecimal> matrixFactory = new BigDecimalMatrixFactoryImpl();
		return matrixFactory.withRandomValues(784, 1, BigDecimal.valueOf(-0.5), BigDecimal.valueOf(0.5));
	}*/
}
