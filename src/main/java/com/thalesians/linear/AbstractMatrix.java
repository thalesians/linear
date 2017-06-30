package com.thalesians.linear;

import com.google.common.base.Preconditions;

public abstract class AbstractMatrix<T> implements Matrix<T> {
	private final MatrixBuilder<T> resultbuilder;
	private Matrix<T> inverse;
	private Matrix<T> transpose;
	
	public AbstractMatrix(MatrixBuilder<T> resultbuilder) {
		this.resultbuilder = resultbuilder;
	}
	
	protected MatrixBuilder<T> getResultBuilder() {
		return resultbuilder;
	}
	
	@Override public Matrix<T> get(Rectangle rect) {
		return SimpleSubMatrixView.of(this, rect, resultbuilder);
	}
	
	@Override public Matrix<T> scale(T scalar) {
		return resultbuilder.set(this).scale(scalar).build();
	}
	
	@Override public Matrix<T> add(Matrix<T> matrix) {
		return resultbuilder.set(this).add(matrix).build();
	}
	
	@Override public Matrix<T> subtract(Matrix<T> matrix) {
		return resultbuilder.set(this).subtract(matrix).build();
	}
	
	@Override public Matrix<T> mult(Matrix<T> matrix) {
		return resultbuilder.setShape(getRowCount(), matrix.getColumnCount()).mult(this, matrix).build();
	}
	
	@Override public Matrix<T> multByDiag(Matrix<T> diag) {
		return resultbuilder.set(this).multByDiag(diag).build();
	}
	
	@Override public Matrix<T> vec() {
		int rc = getRowCount(), cc = getColumnCount();
		resultbuilder.setShape(rc * cc, 1);
		int rowoffset = 0;
		for (int j = 0; j < cc; ++j) {
			for (int i = 0; i < rc; ++i) {
				resultbuilder.set(rowoffset++, 0, get(i, j));
			}
		}
		return resultbuilder.build();
	}
	
	@Override public Matrix<T> unvec(int rowcount) {
		Preconditions.checkArgument(MatrixUtils.isColumnVector(this));
		int rc = getRowCount();
		Preconditions.checkArgument(rc % rowcount == 0);
		int columncount = rc / rowcount;
		resultbuilder.setShape(rowcount, columncount);
		int rowoffset = 0;
		for (int j = 0; j < columncount; ++j) {
			for (int i = 0; i < rowcount; ++i) {
				resultbuilder.set(i, j, get(rowoffset++, 0));
			}
		}
		return resultbuilder.build();
	}
	
	@Override public Matrix<T> kroneckerProduct(Matrix<T> matrix) {
		int rc1 = getRowCount(), cc1 = getColumnCount();
		int rc2 = matrix.getRowCount(), cc2 = matrix.getColumnCount();
		resultbuilder.setShape(rc1 * rc2, cc1 * cc2);
		int rowoffset = 0;
		for (int i = 0; i < rc1; ++i) {
			int columnoffset = 0;
			for (int j = 0; j < cc1; ++j) {
				resultbuilder.set(rowoffset, columnoffset, matrix.scale(get(i, j)));
				columnoffset += cc2;
			}
			rowoffset += rc2;
		}
		return resultbuilder.build();
	}

	@Override public Matrix<T> kroneckerSum(Matrix<T> matrix) {
		Preconditions.checkArgument(MatrixUtils.isSquare(this) && MatrixUtils.isSquare(matrix));
		Matrix<T> eye1 = resultbuilder.setShape(this).setToIdentity().build();
		Matrix<T> eye2 = resultbuilder.setShape(matrix).setToIdentity().build();
		return kroneckerProduct(eye1).add(eye2.kroneckerProduct(matrix));
	}
	
	@Override public Matrix<T> transpose() {
		if (transpose == null) {
			transpose = SimpleTransposeView.of(this, resultbuilder);
		}
		return transpose;
	}
	
	@Override public Matrix<T> invert() {
		if (inverse == null) {
			inverse = resultbuilder.set(this).invert().build();
		}
		return inverse;
	}
	
	@Override public T determinant() {
		throw new UnsupportedOperationException("Cannot calculate determinant for matrix of type " + getClass().getSimpleName());
	}
	
	@Override public String toString() {
		return StringUtils.matrixToString(this);
	}
}
