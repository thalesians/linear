package com.thalesians.linear;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class SimpleMatrix<T> extends AbstractMatrix<T> {
	private final ImmutableList<? extends ImmutableList<? extends T>> data;
	
	public SimpleMatrix(ImmutableList<? extends ImmutableList<? extends T>> data, boolean validate) {
		super(SimpleMatrix.<T>builder());
		if (validate) validate(data);
		this.data = data;
	}
	
	public SimpleMatrix(ImmutableList<? extends ImmutableList<? extends T>> data) {
		this(data, true);
	}

	private static <U> void validate(ImmutableList<? extends ImmutableList<? extends U>> data) {
		for (int i=0, colcount=-1, c=data.size(); i<c; ++i) {
			if (i==0) colcount = data.get(i).size();
			else {
				int cc = data.get(i).size();
				if (colcount != cc) {
					throw new IllegalArgumentException(
							"Row " + i + " has a different number of rows (" + cc +
							") than the previous row (" + colcount + ")");
				}
			}
		}
	}
	
	@Override
	public T get(int row, int column) {
		return data.get(row).get(column);
	}

	@Override
	public int getRowCount() {
		return data.size();
	}
	
	@Override
	public int getColumnCount() {
		return data.size()==0 ? 0 : data.get(0).size();
	}
	
	public static <U> Builder<U> builder() {
		return new Builder<U>();
	}
	
	public static final class Builder<U> implements MatrixBuilder<U> {
		
		private final List<List<U>> impl = Lists.newLinkedList();
		private boolean changed = true;
		private Matrix<U> cached = null;
		
		@Override
		public U get(int row, int column) {
			return impl.get(row).get(column);
		}
		
		@Override
		public MatrixBuilder<U> setShape(int rowcount, int columncount) {
			if (rowcount != impl.size()) {
				changed = true;
				for (int i=0; i<rowcount; ++i) {
					impl.add(Lists.<U>newLinkedList());
				}
			}
			if (rowcount > 0 && columncount != impl.get(0).size()) {
				changed = true;
				for (int i=0; i<rowcount; ++i) {
					List<U> row = impl.get(i);
					row.clear();
					for (int j=0; j<rowcount; ++j) {
						row.add(null);
					}
				}
			}
			return this;
		}
		
		@Override
		public MatrixBuilder<U> setShape(Matrix<?> matrix) {
			return setShape(matrix.getRowCount(), matrix.getColumnCount());
		}
		
		@Override
		public MatrixBuilder<U> setToIdentity() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public MatrixBuilder<U> set(int row, int column, U value) {
			impl.get(row).set(column, value);
			changed = true;
			return this;
		}
		
		@Override
		public MatrixBuilder<U> set(int toprow, int leftcolumn, Matrix<? extends U> matrix) {
			for (int i = 0; i < matrix.getRowCount(); ++i) {
				List<U> row = impl.get(toprow + i);
				for (int j = 0; j < matrix.getColumnCount(); ++j) {
					row.set(leftcolumn + j, matrix.get(i, j));
				}
			}
			changed = true;
			return this;
		}
		
		@Override
		public MatrixBuilder<U> set(Matrix<? extends U> matrix) {
			setShape(matrix);
			for (int i = 0; i < matrix.getRowCount(); ++i) {
				List<U> row = impl.get(i);
				for (int j = 0; j < matrix.getColumnCount(); ++j) {
					row.set(j, matrix.get(i, j));
				}
			}
			changed = true;
			return this;
		}
		
		@Override
		public MatrixBuilder<U> setAll(U value) {
			for (int i = 0; i < impl.size(); ++i) {
				List<U> row = impl.get(i);
				for (int j = 0; j < impl.get(0).size(); ++j) {
					row.set(j, value);
				}
			}
			changed = true;
			return this;
		}
		
		@Override
		public MatrixBuilder<U> setAll(Rectangle rect, U value) {
			for (int i = rect.getTopRow(), ilim = i + rect.getRowCount(); i < ilim; ++i) {
				List<U> row = impl.get(i);
				for (int j = rect.getLeftColumn(), jlim = j + rect.getColumnCount(); j < jlim; ++j) {
					row.set(j, value);
				}
			}
			changed = true;
			return this;
		}
		
		@Override
		public MatrixBuilder<U> scale(int row, int column, U scalar) {
			throw new UnsupportedOperationException();
		}

		@Override
		public MatrixBuilder<U> scale(U scalar) {
			throw new UnsupportedOperationException();
		}

		@Override
		public MatrixBuilder<U> add(int row, int column, U value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public MatrixBuilder<U> add(Matrix<? extends U> matrix) {
			throw new UnsupportedOperationException();
		}

		@Override
		public MatrixBuilder<U> addToDiagonal(Matrix<? extends U> vector) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public MatrixBuilder<U> subtract(Matrix<? extends U> matrix) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public MatrixBuilder<U> mult(Matrix<? extends U> leftmatrix, Matrix<? extends U> rightmatrix) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public MatrixBuilder<U> multByDiag(Matrix<? extends U> diag) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public MatrixBuilder<U> multElementwise(Matrix<? extends U> matrix) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public MatrixBuilder<U> invert() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Matrix<U> build() {
			if (changed) {
				ImmutableList.Builder<ImmutableList<U>> b = ImmutableList.builder();
				for (List<U> r : impl) {
					b.add(ImmutableList.copyOf(r));
				}
				cached = new SimpleMatrix<U>(b.build());
			}
			return cached;
		}
		
	}
}
