package com.thalesians.linear;

public final class SimpleSubMatrixView<T> extends AbstractMatrix<T> {
	private final Matrix<T> matrix;

	private final Rectangle rect;
	
	public SimpleSubMatrixView(Matrix<T> matrix, Rectangle rect, MatrixBuilder<T> resultbuilder) {
		super(resultbuilder);
		this.matrix = matrix;
		this.rect = rect;
	}
	
	public static <E> SimpleSubMatrixView<E> of(Matrix<E> matrix, Rectangle rect, MatrixBuilder<E> resultbuilder) {
		return new SimpleSubMatrixView<E>(matrix, rect, resultbuilder);
	}
	
	@Override public T get(int row, int column) {
		return matrix.get(rect.getTopRow() + row, rect.getLeftColumn() + column);
	}
	
	@Override public Matrix<T> get(Rectangle rect) {
		return of(matrix, SimpleRectangle.create(
				this.rect.getTopRow() + rect.getTopRow(), this.rect.getLeftColumn() + rect.getLeftColumn(),
				rect.getRowCount(), rect.getColumnCount()), getResultBuilder());
	}
	
	@Override public int getRowCount() {
		return rect.getRowCount();
	}
	
	@Override public int getColumnCount() {
		return rect.getColumnCount();
	}
	
	@Override public T determinant() {
		return matrix.get(rect).determinant();
	}
}
