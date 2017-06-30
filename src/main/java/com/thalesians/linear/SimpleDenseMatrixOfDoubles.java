package com.thalesians.linear;

import org.ejml.alg.dense.mult.MatrixDimensionException;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

public final class SimpleDenseMatrixOfDoubles extends AbstractMatrix<Double> {
	final DenseMatrix64F impl;
	
	private Double determinant;
	
	SimpleDenseMatrixOfDoubles(DenseMatrix64F impl) {
		super(builder());
		this.impl = impl;
	}
	
	public static SimpleDenseMatrixOfDoubles of(double[][] data) {
		// This constructor of DenseMatrix64F will copy the data
		return new SimpleDenseMatrixOfDoubles(new DenseMatrix64F(data));
	}
	
	public static SimpleDenseMatrixOfDoubles of(double data) {
		return of(new double[][]{ {data} });
	}
	
	public static SimpleDenseMatrixOfDoubles copyOf(DenseMatrix64F matrix) {
		return matrix == null ? null : new SimpleDenseMatrixOfDoubles(matrix.copy());
	}
	
	public static SimpleDenseMatrixOfDoubles copyOf(Matrix<Double> matrix) {
		if (matrix == null) { return null; }
		return builderFromCopy(matrix).build();
	}
	
	public static SimpleDenseMatrixOfDoubles rowVector(double[] data) {
		return of(new double[][]{ data });
	}
	
	public static SimpleDenseMatrixOfDoubles rowVector(Double... data) {
		double[] d = new double[data.length];
		for (int i = 0; i < data.length; ++i) d[i] = data[i];
		return rowVector(d);
	}
	
	public static SimpleDenseMatrixOfDoubles columnVector(double[] data) {
		double[][] d = new double[data.length][];
		for (int i = 0; i < data.length; ++i) d[i] = new double[] { data[i] };
		return of(d);
	}
	
	public static SimpleDenseMatrixOfDoubles columnVector(Double... data) {
		double[][] d = new double[data.length][];
		for (int i = 0; i < data.length; ++i) d[i] = new double[] { data[i] };
		return of(d);
	}
	
	public static SimpleDenseMatrixOfDoubles identity(int dimensioncount) {
		return builder(dimensioncount, dimensioncount).setToIdentity().build();
	}
	
	public static SimpleDenseMatrixOfDoubles zero(int rowcount, int columncount) {
		return builder(rowcount, columncount).build();
	}
	
	public static SimpleDenseMatrixOfDoubles zero(int dimensioncount) {
		return zero(dimensioncount, dimensioncount);
	}
	
	public static SimpleDenseMatrixOfDoubles standardBasisRowVector(int dimensioncount, int index) {
		return builder(1, dimensioncount).set(0, index, 1.0).build();
	}
	
	public static SimpleDenseMatrixOfDoubles standardBasisColumnVector(int dimensioncount, int index) {
		return builder(dimensioncount, 1).set(index, 0, 1.0).build();
	}
	
	@Override public Double get(int row, int column) {
		return impl.unsafe_get(row, column);
	}
	
	@Override public Matrix<Double> get(Rectangle rect) {
		DenseMatrix64F ret = new DenseMatrix64F(rect.getRowCount(), rect.getColumnCount());
		CommonOps.extract(impl, rect.getTopRow(), rect.getTopRow() + rect.getRowCount(), rect.getLeftColumn(), rect.getLeftColumn() + rect.getColumnCount(), ret, 0, 0);
		return new SimpleDenseMatrixOfDoubles(ret);
	}
	
	@Override public int getRowCount() {
		return impl.numRows;
	}
	
	@Override public int getColumnCount() {
		return impl.numCols;
	}
	
	@Override public Double determinant() {
		if (determinant == null) {
			determinant = CommonOps.det(impl);
		}
		return determinant;
	}
	
	@Override public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj.getClass() != getClass()) return false;
		SimpleDenseMatrixOfDoubles rhs = (SimpleDenseMatrixOfDoubles) obj;
		return impl.equals(rhs.impl);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(int rowcount, int columncount) {
		return (new Builder()).setShape(rowcount, columncount);
	}
	
	public static Builder builderFromCopy(Matrix<? extends Double> matrix) {
		return (new Builder()).set(matrix);
	}
	
	public static class Builder implements MatrixBuilder<Double> {
		
		private DenseMatrix64F impl = new DenseMatrix64F(new double[][] {{0}});
		private boolean changed = true;
		private SimpleDenseMatrixOfDoubles cached = null;
		
		private Builder() { }
		
		@Override public Double get(int row, int column) {
			return impl.unsafe_get(row, column);
		}
		
		@Override public Builder setShape(int rowcount, int columncount) {
			if (impl.numRows != rowcount || impl.numCols != columncount) {
				impl.reshape(rowcount, columncount, false);
				changed = true;
			}
			return this;
		}
		
		@Override public Builder setShape(Matrix<?> matrix) {
			return setShape(matrix.getRowCount(), matrix.getColumnCount());
		}

		@Override public Builder setToIdentity() {
			CommonOps.setIdentity(impl);
			changed = true;
			return this;
		}

		@Override public Builder set(int row, int column, Double value) {
			impl.unsafe_set(row, column, value);
			changed = true;
			return this;
		}

		@Override public Builder set(int toprow, int leftcolumn, Matrix<? extends Double> matrix) {
			for (int i = 0; i < matrix.getRowCount(); ++i) {
				for (int j = 0; j < matrix.getColumnCount(); ++j) {
					impl.unsafe_set(toprow + i, leftcolumn + j, matrix.get(i, j));
				}
			}
			changed = true;
			return this;
		}

		@Override public Builder set(Matrix<? extends Double> matrix) {
			setShape(matrix);
			for (int i = 0; i < matrix.getRowCount(); ++i) {
				for (int j = 0; j < matrix.getColumnCount(); ++j) {
					impl.unsafe_set(i, j, matrix.get(i, j));
				}
			}
			changed = true;
			return this;
		}
		
		@Override public Builder setAll(Double value) {
			for (int i = 0; i < impl.numRows; ++i) {
				for (int j = 0; j < impl.numCols; ++j) {
					impl.unsafe_set(i, j, value);
				}
			}
			changed = true;
			return this;
		}
		
		public Builder setAllToNaN() {
			setAll(Double.NaN);
			return this;
		}
		
		@Override public Builder setAll(Rectangle rect, Double value) {
			for (int i = rect.getTopRow(), ilim = i + rect.getRowCount(); i < ilim; ++i) {
				for (int j = rect.getLeftColumn(), jlim = j + rect.getColumnCount(); j < jlim; ++j) {
					impl.unsafe_set(i, j, value);
				}
			}
			changed = true;
			return this;
		}

		@Override public Builder scale(int row, int column, Double scalar) {
			impl.unsafe_set(row, column, scalar * impl.get(row, column));
			changed = true;
			return this;
		}

		@Override public Builder scale(Double scalar) {
			CommonOps.scale(scalar, impl);
			changed = true;
			return this;
		}

		@Override public Builder add(int row, int column, Double value) {
			impl.add(row, column, value);
			changed = true;
			return this;
		}

		@Override public Builder add(Matrix<? extends Double> matrix) {
			if (matrix instanceof SimpleDenseMatrixOfDoubles) {
				DenseMatrix64F m = ((SimpleDenseMatrixOfDoubles) matrix).impl;
				CommonOps.addEquals(impl, m);
			} else {
				for (int i = 0; i < matrix.getRowCount(); ++i) {
					for (int j = 0; j < matrix.getColumnCount(); ++j) {
						impl.add(i, j, matrix.get(i, j));
					}
				}
			}
			changed = true;
			return this;
		}

		@Override public Builder addToDiagonal(Matrix<? extends Double> vector) {
			for (int i = 0; i < Math.min(vector.getRowCount(), Math.min(impl.numRows, impl.numCols)); ++i) {
				impl.add(i, i, vector.get(i, 0));
			}
			changed = true;
			return this;
		}

		@Override public Builder subtract(Matrix<? extends Double> matrix) {
			if (matrix instanceof SimpleDenseMatrixOfDoubles) {
				DenseMatrix64F m = ((SimpleDenseMatrixOfDoubles) matrix).impl;
				CommonOps.subEquals(impl, m);
			} else {
				for (int i = 0; i < matrix.getRowCount(); ++i) {
					for (int j = 0; j < matrix.getColumnCount(); ++j) {
						impl.add(i, j, -matrix.get(i, j));
					}
				}
			}
			changed = true;
			return this;
		}

		@Override public Builder mult(Matrix<? extends Double> leftmatrix, Matrix<? extends Double> rightmatrix) {
			setShape(leftmatrix.getRowCount(), rightmatrix.getColumnCount());
			DenseMatrix64F lm = MatrixUtils.toEJMLDenseMatrix64F(leftmatrix);
			DenseMatrix64F rm = MatrixUtils.toEJMLDenseMatrix64F(rightmatrix);
			try {
				CommonOps.mult(lm, rm, impl);
			} catch (MatrixDimensionException e) {
				throw new RuntimeLinearException(new StringBuilder().append("Incompatible matrix dimensions for multiplication (lhs: ").append(lm.numRows).append(" x ").append(lm.numCols).append(", rhs: ").append(rm.numRows).append(" x ").append(rm.numCols).append(")").toString());
			}
			changed = true;
			return this;
		}
		
		@Override public Builder multByDiag(Matrix<? extends Double> diag) {
			for (int i = 0; i < impl.numRows; ++i) {
				for (int j = 0; j < impl.numCols; ++j) {
					impl.unsafe_set(i, j, impl.unsafe_get(i, j) * diag.get(j, 0));
				}
			}
			changed = true;
			return this;
		}
		
		@Override public Builder multElementwise(Matrix<? extends Double> matrix) {
			for (int i = 0; i < matrix.getRowCount(); ++i) {
				for (int j = 0; j < matrix.getColumnCount(); ++j) {
					impl.times(impl.getIndex(i, j), matrix.get(i, j));
				}
			}
			changed = true;
			return this;
		}

		@Override public Builder invert() {
			if (!CommonOps.invert(impl)) {
				throw new RuntimeLinearException("Singular matrix");
			}
			changed = true;
			return this;
		}

		@Override public SimpleDenseMatrixOfDoubles build() {
			if (changed) {
				cached = new SimpleDenseMatrixOfDoubles(impl.copy());
				changed = false;
			}
			return cached;
		}
	}
	
	
	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < impl.numRows; ++i) {
			for (int j = 0; j < impl.numCols; ++j) {
				if (j > 0) sb.append(", ");
				sb.append(impl.unsafe_get(i, j));
			}
			if (i < impl.numRows - 1) sb.append(";");
		}
		sb.append("]");
		return sb.toString();
	}
}
