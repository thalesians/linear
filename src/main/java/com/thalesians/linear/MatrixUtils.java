package com.thalesians.linear;

import org.ejml.data.DenseMatrix64F;

public final class MatrixUtils {
	private MatrixUtils() {
		throw new AssertionError("This class cannot be instantiated");
	}
	
	public static double[] toArrayOfDoubles(Matrix<Double> matrix) {
		int rc = matrix.getRowCount();
		int cc = matrix.getColumnCount();
		double[] result = new double[rc * cc];
		for (int i = 0; i < rc; ++i) {
			for (int j = 0; j < cc; ++j) {
				result[i * cc + j] = matrix.get(i, j);
			}
		}
		return result;
	}
	
	public static int[] toArrayOfInts(Matrix<Double> matrix) {
		int rc = matrix.getRowCount();
		int cc = matrix.getColumnCount();
		int[] result = new int[rc * cc];
		for (int i = 0; i < rc; ++i) {
			for (int j = 0; j < cc; ++j) {
				result[i * cc + j] = matrix.get(i, j).intValue();
			}
		}
		return result;
	}
	
	public static Object[][] to2DArray(Matrix<?> matrix) {
		int rc = matrix.getRowCount();
		int cc = matrix.getColumnCount();
		Object[][] result = new Object[rc][];
		for (int i = 0; i < rc; ++i) {
			result[i] = new Object[cc];
			for (int j = 0; j < cc; ++j) {
				result[i][j] = matrix.get(i, j);
			}
		}
		return result;
	}
	
	public static double[][] to2DArrayOfDoubles(Matrix<Double> matrix) {
		int rc = matrix.getRowCount();
		int cc = matrix.getColumnCount();
		double[][] result = new double[rc][];
		for (int i = 0; i < rc; ++i) {
			result[i] = new double[cc];
			for (int j = 0; j < cc; ++j) {
				result[i][j] = matrix.get(i, j);
			}
		}
		return result;
	}
	
	public static <T> boolean isScalar(Matrix<T> matrix) {
		return matrix.getRowCount() == 1 && matrix.getColumnCount() == 1;
	}
	
	public static <T> boolean isRowVector(Matrix<T> matrix) {
		return matrix != null && matrix.getRowCount() == 1;
	}
	
	public static <T> boolean isColumnVector(Matrix<T> matrix) {
		return matrix != null && matrix.getColumnCount() == 1;
	}
	
	public static <ValueType> boolean isSquare(Matrix<ValueType> matrix) {
		return matrix.getRowCount() == matrix.getColumnCount();
	}
	
	public static <T1, T2> boolean areSameShape(Matrix<T1> matrix1, Matrix<T2> matrix2) {
		return matrix1 == matrix2 ||
				(matrix1 != null && matrix2 != null && matrix1.getRowCount() == matrix2.getRowCount() && matrix1.getColumnCount() == matrix2.getColumnCount());
	}
	
	public static <T1, T2> boolean areCompatible(Matrix<T1> matrix1, Matrix<T2> matrix2) {
		return matrix1 != null && matrix2 != null && matrix1.getColumnCount() == matrix2.getRowCount();
	}
	
	public static boolean isValidIndex(Matrix<?> matrix, int row, int column) {
		return row >= 0 && row < matrix.getRowCount() && column >= 0 && column < matrix.getColumnCount();
	}
	
	public static boolean isValidRectangle(Matrix<?> matrix, Rectangle rect) {
		return rect != null && rect.getTopRow() >= 0 && rect.getLeftColumn() >= 0
				&& rect.getTopRow() + rect.getRowCount() <= matrix.getRowCount() && rect.getLeftColumn() + rect.getColumnCount() <= matrix.getColumnCount();
	}
	
	public static boolean isSymmetric(Matrix<? extends Double> matrix, double tol) {
		double max = maxAbs(matrix);
		for (int i = 0, rc = matrix.getRowCount(); i < rc; ++i) {
			for (int j = 0, cc = matrix.getColumnCount(); j < cc; ++j) {
				double a = matrix.get(i, j) / max;
				double b = matrix.get(j, i) / max;
				
				double diff = Math.abs(a - b);
				
				if (!(diff <= tol)) return false;
			}
		}
		return true;
	}
	
	public static boolean isSymmetric(Matrix<? extends Double> matrix) {
		if (matrix instanceof SymmetricMatrix) {
			return true;
		} else if (!isSquare(matrix)) {
			return false;
		} else {
			return isSymmetric(matrix, ArithmeticsUtils.getMachineEpsilonDouble());
		}
	}
	
	public static Matrix<Double> replaceNaNs(Matrix<? extends Double> matrix, double value) {
		MatrixBuilder<Double> b = SimpleDenseMatrixOfDoubles.builder().setShape(matrix);
		for (int i = 0, rc = matrix.getRowCount(); i < rc; ++i) {
			for (int j = 0, cc = matrix.getColumnCount(); j < cc; ++j) {
				double v = matrix.get(i, j);
				b.set(i, j, Double.isNaN(v) ? value : v);
			}
		}
		return b.build();
	}
	
	public static double maxAbs(Matrix<? extends Double> matrix) {
		double max = 0.0;
		for (int i = 0, rc = matrix.getRowCount(); i < rc; ++i) {
			for (int j = 0, cc = matrix.getColumnCount(); j < cc; ++j) {
				double val = Math.abs(matrix.get(i, j));
				if (val > max) {
					max = val;
				}
			}
		}
		return max;
	}
	
	public static double minAbs(Matrix<? extends Double> matrix) {
		double min = Double.MAX_VALUE;
		for (int i = 0, rc = matrix.getRowCount(); i < rc; ++i) {
			for (int j = 0, cc = matrix.getColumnCount(); j < cc; ++j) {
				double val = Math.abs(matrix.get(i, j));
				if (val < min) {
					min = val;
				}
			}
		}
		return min;
	}
	
	public static <E> Matrix<E> getDiagonal(Matrix<E> matrix, MatrixBuilder<E> builder) {
		int d = Math.min(matrix.getRowCount(), matrix.getColumnCount());
		builder.setShape(d, 1);
		for (int i=0; i<d; ++i) {
			builder.set(i, 0, matrix.get(i, i));
		}
		return builder.build();
	}
	
	public static <E> Matrix<E> replaceDiagonal(Matrix<E> matrix, Matrix<E> diag, MatrixBuilder<E> builder) {
		int rc = matrix.getRowCount(), cc = matrix.getColumnCount();
		int d = Math.min(diag.getRowCount(), Math.min(rc, cc));
		builder.setShape(rc, cc);
		for (int i=0; i<rc; ++i) {
			for (int j=0, jj = Math.min(i, cc); j<jj; ++j) {
				builder.set(i, j, matrix.get(i, j));
			}
			if (i<cc) {
				builder.set(i, i, i<d ? diag.get(i, 0) : matrix.get(i, i));
			}
			for (int j=i+1, jj=cc; j<jj; ++j) {
				builder.set(i, j, matrix.get(i, j));
			}
		}
		return builder.build();
	}
	
	static DenseMatrix64F toEJMLDenseMatrix64F(Matrix<? extends Double> matrix) {
		if (matrix instanceof SimpleDenseMatrixOfDoubles) {
			return ((SimpleDenseMatrixOfDoubles) matrix).impl;
		} else {
			return SimpleDenseMatrixOfDoubles.builderFromCopy(matrix).build().impl;
		}
	}	
}
