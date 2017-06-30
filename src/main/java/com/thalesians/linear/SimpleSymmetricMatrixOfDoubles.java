package com.thalesians.linear;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Preconditions;

public final class SimpleSymmetricMatrixOfDoubles extends AbstractMatrix<Double> implements SymmetricMatrix<Double> {
	protected final int rowcount;
	protected final double[] data;
	protected final double scale;
	
	protected SimpleSymmetricMatrixOfDoubles(int rowcount, double[] data, double scale) {
		super(SimpleDenseMatrixOfDoubles.builder());
		this.rowcount = rowcount;
		this.data = Preconditions.checkNotNull(data);
		this.scale = scale;
	}
	
	protected SimpleSymmetricMatrixOfDoubles(int rowcount, double[] data) {
		this(rowcount, data, 1.0);
	}

	protected SimpleSymmetricMatrixOfDoubles(SimpleSymmetricMatrixOfDoubles matrix) {
		this(matrix.rowcount, matrix.data, matrix.scale);
	}
	
	protected SimpleSymmetricMatrixOfDoubles(Matrix<Double> matrix) {
		super(SimpleDenseMatrixOfDoubles.builder());
		this.rowcount = matrix.getRowCount();
		this.data = new double[getDataLength(rowcount)];
		this.scale = 1.0;
		for (int i = 0; i < matrix.getRowCount(); ++i) {
			for (int j = 0; j <= i; ++j) {
				data[getDataIndex(i, j)] = matrix.get(i, j);
			}
		}
	}
	
	protected static int getDataLength(int rowcount) {
		return (rowcount * rowcount + rowcount) / 2;
	}
	
	protected static int getDataIndex(int row, int column) {
		if (column > row) {
			return getDataIndex(column, row);
		} else {
			return (row * row + row) / 2 + column;
		}
	}

	public static SimpleSymmetricMatrixOfDoubles create(int dimcount, double value) {
		return builder(dimcount).setAll(value).build();
	}
	
	public static SimpleSymmetricMatrixOfDoubles identity(int dimcount) {
		return builder(dimcount).setToIdentity().build();
	}
	
	public static SimpleSymmetricMatrixOfDoubles zero(int dimcount) {
		return create(dimcount, 0.);
	}
	
	public static SimpleSymmetricMatrixOfDoubles nan(int dimcount) {
		return create(dimcount, Double.NaN);
	}
	
	public static SimpleSymmetricMatrixOfDoubles fromLower(Matrix<Double> matrix) {
		if (matrix instanceof SimpleSymmetricMatrixOfDoubles) {
			return new SimpleSymmetricMatrixOfDoubles((SimpleSymmetricMatrixOfDoubles) matrix);
		} else {
			return new SimpleSymmetricMatrixOfDoubles(matrix);
		}
	}
	
	public static SimpleSymmetricMatrixOfDoubles fromUpper(Matrix<Double> matrix) {
		if (matrix instanceof SimpleSymmetricMatrixOfDoubles) {
			return new SimpleSymmetricMatrixOfDoubles((SimpleSymmetricMatrixOfDoubles) matrix);
		} else {
			return new SimpleSymmetricMatrixOfDoubles(matrix.transpose());
		}
	}

	@Override public Double get(int row, int column) {
		return scale * data[getDataIndex(row, column)];
	}
	
	@Override public int getRowCount() {
		return rowcount;
	}
	
	@Override public int getColumnCount() {
		return rowcount;
	}
	
	@Override public Matrix<Double> transpose() {
		return this;
	}
	
	@Override public Matrix<Double> scale(Double scalar) {
		return new SimpleSymmetricMatrixOfDoubles(rowcount, data, scale * scalar);
	}
	
	@Override public Matrix<Double> add(Matrix<Double> matrix) {
		if (MatrixUtils.isSymmetric(matrix)) {
			return getResultBuilder().set(this).add(matrix).build();
		} else {
			return super.add(matrix);
		}
	}
	
	@Override public Matrix<Double> subtract(Matrix<Double> matrix) {
		if (MatrixUtils.isSymmetric(matrix)) {
			return getResultBuilder().set(this).subtract(matrix).build();
		} else {
			return SimpleDenseMatrixOfDoubles.builder().setShape(getRowCount(), getColumnCount()).set(this).subtract(matrix).build();
		}		
	}
	
	@Override public Double determinant() {
		return SimpleDenseMatrixOfDoubles.builderFromCopy(this).build().determinant();
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(int dimcount) {
		return new Builder().setShape(dimcount, dimcount);
	}
	
	public static Builder builderFromLower(Matrix<? extends Double> matrix) {
		return new Builder().setFromLower(matrix);
	}
	
	public static Builder builderFromUpper(Matrix<? extends Double> matrix) {
		return new Builder().setFromUpper(matrix);
	}
	
	public static class Builder implements MatrixBuilder<Double> {
		
		private int rowcount;
		private double[] impl;
		private boolean changed = true;
		private SimpleSymmetricMatrixOfDoubles cached = null;
		
		@Override
		public Double get(int row, int column) {
			return impl[getDataIndex(row, column)];
		}
		
		@Override public Builder setShape(int rowcount, int columncount) {
			checkArgument(rowcount == columncount);
			this.rowcount = rowcount;
			this.impl = new double[getDataLength(rowcount)];
			this.changed = true;
			return this;
		}
		
		public Builder setShape(int dimcount) {
			return setShape(dimcount, dimcount);
		}
		
		@Override public Builder setShape(Matrix<?> matrix) {
			return setShape(matrix.getRowCount(), matrix.getColumnCount());
		}
		
		@Override public Builder setToIdentity() {
			int dataindex = 0;
			for (int i = 0; i < rowcount; ++i) {
				for (int j = 0; j < i; ++j) {
					impl[dataindex++] = 0.0;
				}
				impl[dataindex] = 1.0;
			}
			changed = true;
			return this;
		}
		
		@Override public Builder set(int row, int column, Double value) {
			impl[getDataIndex(row, column)] = value;
			changed = true;
			return this;
		}
		
		@Override public Builder set(int toprow, int leftcolumn, Matrix<? extends Double> matrix) {
			for (int i = 0; i < matrix.getRowCount(); ++i) {
				for (int j = 0; j <= i; ++j) {
					set(toprow + i, leftcolumn + j, matrix.get(i, j));
				}
			}
			changed = true;
			return this;
		}
		
		public Builder setFromLower(Matrix<? extends Double> matrix) {
			setShape(matrix);
			for (int i = 0; i < matrix.getRowCount(); ++i) {
				for (int j = 0; j <= i; ++j) {
					set(i, j, matrix.get(i, j));
				}
			}
			changed = true;
			return this;
		}

		public Builder setFromUpper(Matrix<? extends Double> matrix) {
			setShape(matrix);
			for (int i = 0; i < matrix.getRowCount(); ++i) {
				for (int j = 0; j <= i; ++j) {
					set(i, j, matrix.get(j, i));
				}
			}
			changed = true;
			return this;
		}
		
		@Override public Builder set(Matrix<? extends Double> matrix) {
			setFromLower(matrix);
			changed = true;
			return this;
		}
		
		@Override public Builder setAll(Double value) {
			for (int i = 0; i < impl.length; ++i) {
				impl[i] = value;
			}
			changed = true;
			return this;
		}
		
		@Override public Builder setAll(Rectangle rect, Double value) {
			for (int i = rect.getTopRow(), ilim = i + rect.getRowCount(); i < ilim; ++i) {
				for (int j = rect.getLeftColumn(), jlim = j + rect.getColumnCount(); j < jlim; ++j) {
					impl[getDataIndex(i, j)] = value;
				}
			}
			changed = true;
			return this;
		}
		
		@Override public Builder scale(int row, int column, Double value) {
			impl[getDataIndex(row, column)] *= value;
			changed = true;
			return this;
		}
		
		@Override public Builder scale(Double scalar) {
			for (int i = 0; i < impl.length; ++i) {
				impl[i] *= scalar;
			}
			changed = true;
			return this;
		}
		
		@Override public Builder add(int row, int column, Double value) {
			impl[getDataIndex(row, column)] += value;
			changed = true;
			return this;
		}
		
		@Override public Builder add(Matrix<? extends Double> matrix) {
			for (int i = 0; i < matrix.getRowCount(); ++i) {
				for (int j = 0; j <= i; ++j) {
					impl[getDataIndex(i, j)] += matrix.get(i, j);
				}
			}
			changed = true;
			return this;
		}
		
		@Override public Builder addToDiagonal(Matrix<? extends Double> vector) {
			for (int i = 0; i < rowcount; ++i) {
				impl[getDataIndex(i, i)] = vector.get(i, 0);
			}
			changed = true;
			return this;
		}

		@Override public Builder subtract(Matrix<? extends Double> matrix) {
			for (int i = 0; i < matrix.getRowCount(); ++i) {
				for (int j = 0; j <= i; ++j) {
					impl[getDataIndex(i, j)] -= matrix.get(i, j);
				}
			}
			changed = true;
			return this;
		}
		
		@Override public Builder mult(Matrix<? extends Double> leftmatrix, Matrix<? extends Double> rightmatrix) {
			throw new UnsupportedOperationException("The symmetric matrix builder does not support matrix multiplication");
		}
		
		@Override public Builder multByDiag(Matrix<? extends Double> diag) {
			for (int i = 0; i < rowcount; ++i) {
				for (int j = 0; j <= i; ++j) {
					impl[getDataIndex(i, j)] *= diag.get(j, 0);
				}
			}
			changed = true;
			return this;
		}
		
		@Override public Builder multElementwise(Matrix<? extends Double> matrix) {
			if (!MatrixUtils.isSymmetric(matrix)) {
				throw new UnsupportedOperationException("The symmetric matrix builder does not support elementwise multiplication by a nonsymmetric matrix");
			} else {
				for (int i = 0; i < rowcount; ++i) {
					for (int j = 0; j <= i; ++j) {
						impl[getDataIndex(i, j)] *= matrix.get(i, j);
					}
				}
			}
			changed = true;
			return this;
		}
		
		@Override public Builder invert() {
			// TODO this can be done more efficiently
			setFromLower(SimpleDenseMatrixOfDoubles.builderFromCopy(build()).invert().build());
			changed = true;
			return this;
		}
		
		@Override public SimpleSymmetricMatrixOfDoubles build() {
			if (changed) {
				cached = new SimpleSymmetricMatrixOfDoubles(rowcount, impl);
				changed = false;
			}
			return cached;
		}
	}
}
