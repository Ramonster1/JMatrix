package org.jmatrix.matrix.dot;

public class IntegerDotFunctions extends DotHelperFunctions<Integer> {

	private static volatile IntegerDotFunctions instance;

	private IntegerDotFunctions() {
		super((x, y) -> x * y, 0, Integer::sum);
	}

	public static IntegerDotFunctions getInstance() {
		IntegerDotFunctions result = instance;
		if (result != null) {
			return result;
		}

		synchronized (IntegerDotFunctions.class) {
			if (instance == null) {
				instance = new IntegerDotFunctions();
			}
			return instance;
		}
	}
}
