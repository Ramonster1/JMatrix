package org.jmatrix.matrix.dot;

import java.util.function.BiFunction;

/**
 * TODO
 * 	Refactor to be more Object-orientated, e.g. make Matrix a subclass of an interface that has these functions.
 * 	Could also rename to MathFunctions
 * @param <T>
 */
public class DotHelperFunctions<T extends Number> {

	private final T zeroVal;
	private final BiFunction<T, T, T> multiplyFunction;
	private final BiFunction<T, T, T> accumulatorFunction;

	public DotHelperFunctions(BiFunction<T, T, T> multiplyFunction, T zeroVal, BiFunction<T, T, T> accumulatorFunction) {
		this.multiplyFunction = multiplyFunction;
		this.zeroVal = zeroVal;
		this.accumulatorFunction = accumulatorFunction;
	}

	public BiFunction<T, T, T> getMultiplyFunction() {
		return multiplyFunction;
	}

	public T getZeroVal() {
		return zeroVal;
	}

	public BiFunction<T, T, T> getAccumulatorFunction() {
		return accumulatorFunction;
	}
}
