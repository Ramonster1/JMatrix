module org.jmatrix.matrix {
	requires org.apache.logging.log4j;
	// requires jmh.core;

	exports org.jmatrix.matrix.concurrent.task;
	exports org.jmatrix.matrix.dot;
	exports org.jmatrix.matrix.exception;
	exports org.jmatrix.matrix.matrixFactory;
	exports org.jmatrix.matrix.matrix;
	exports org.jmatrix.matrix.concurrent;
}