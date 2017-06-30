package com.thalesians.linear;

public final class MatrixArithmetics {
	private MatrixArithmetics() {
		throw new AssertionError("This class cannot be instantiated");
	}

	public static Matrix<Double> sumOfRows(Matrix<Double> matrix) {
		int rc = matrix.getRowCount(), cc = matrix.getColumnCount();
		SimpleDenseMatrixOfDoubles.Builder result = SimpleDenseMatrixOfDoubles.builder(1, cc);
		for (int i = 0; i < rc; ++i) {
			for (int j = 0; j < cc; ++j) {
				result.add(0, j, matrix.get(i, j));
			}
		}
		return result.build();
	}
	
	public static Matrix<Double> sumOfColumns(Matrix<Double> matrix) {
		int rc = matrix.getRowCount(), cc = matrix.getColumnCount();
		SimpleDenseMatrixOfDoubles.Builder result = SimpleDenseMatrixOfDoubles.builder(rc, 1);
		for (int i = 0; i < rc; ++i) {
			for (int j = 0; j < cc; ++j) {
				result.add(i, 0, matrix.get(i, j));
			}
		}
		return result.build();
	}
	
	public static Matrix<Double> meanRow(Matrix<Double> matrix) {
		int rc = matrix.getRowCount(), cc = matrix.getColumnCount();
		SimpleDenseMatrixOfDoubles.Builder result = SimpleDenseMatrixOfDoubles.builder(1, cc);
		for (int i = 0; i < rc; ++i) {
			for (int j = 0; j < cc; ++j) {
				result.add(0, j, (matrix.get(i, j) - result.get(0, j)) / ((double) (i + 1)));
			}
		}
		return result.build();
	}
	
	public static Matrix<Double> meanColumn(Matrix<Double> matrix) {
		int rc = matrix.getRowCount(), cc = matrix.getColumnCount();
		SimpleDenseMatrixOfDoubles.Builder result = SimpleDenseMatrixOfDoubles.builder(rc, 1);
		for (int i = 0; i < rc; ++i) {
			for (int j = 0; j < cc; ++j) {
				result.add(1, 0, (matrix.get(i, j) - result.get(i, 0)) / ((double) (j + 1)));
			}
		}
		return result.build();
	}
	
	public static double meanElement(Matrix<Double> matrix) {
		int rc = matrix.getRowCount(), cc = matrix.getColumnCount();
		double result = 0.0;
		int index = 0;
		for (int i = 0; i < rc; ++i) {
			for (int j = 0; j < cc; ++j) {
				result += (matrix.get(i, j) - result) / ((double) (index + 1));
				++index;
			}
		}
		return result;
	}
}
