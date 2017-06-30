package com.thalesians.linear;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

public final class Elementwise {
	private Elementwise() {
		throw new AssertionError("This class cannot be instantiated");
	}
	
	public static <T> Matrix<T> elementwise(MatrixBuilder<T> builder, Matrix<T> matrix, Function<T, T> func) {
		int rc = matrix.getRowCount(), cc = matrix.getColumnCount();
		builder.setShape(rc, cc);
		for (int i = 0; i < rc; ++i) {
			for (int j = 0; j < cc; ++j) {
				builder.set(i, j, func.apply(matrix.get(i, j)));
			}
		}
		return builder.build();
	}
	
	public static Matrix<Double> elementwise(Matrix<Double> matrix, Function<Double, Double> func) {
		return elementwise(SimpleDenseMatrixOfDoubles.builder(), matrix, func);
	}
	
	public static <T> Matrix<T> elementwise(MatrixBuilder<T> builder, Matrix<T> matrix1, Matrix<T> matrix2, Function2<T, T, T> func) {
		Preconditions.checkArgument(MatrixUtils.areSameShape(matrix1, matrix2));
		int rc = matrix1.getRowCount(), cc = matrix1.getColumnCount();
		builder.setShape(rc, cc);
		for (int i = 0; i < rc; ++i) {
			for (int j = 0; j < cc; ++j) {
				builder.set(i, j, func.apply(matrix1.get(i, j), matrix2.get(i, j)));
			}
		}
		return builder.build();
	}
	
	public static Matrix<Double> elementwise(Matrix<Double> matrix1, Matrix<Double> matrix2, Function2<Double, Double, Double> func) {
		return elementwise(SimpleDenseMatrixOfDoubles.builder(), matrix1, matrix2, func);
	}
}
