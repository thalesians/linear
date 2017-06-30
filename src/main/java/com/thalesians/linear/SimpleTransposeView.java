package com.thalesians.linear;

public final class SimpleTransposeView<T> extends AbstractMatrix<T> {
	private final Matrix<T> matrix;

	public SimpleTransposeView(Matrix<T> matrix, MatrixBuilder<T> resultbuilder) {
		super(resultbuilder);
		this.matrix = matrix;
	}
	
	public static <E> SimpleTransposeView<E> of(Matrix<E> matrix, MatrixBuilder<E> resultbuilder) {
		return new SimpleTransposeView<E>(matrix, resultbuilder);
	}
	
	public static SimpleTransposeView<Double> of(Matrix<Double> matrix) {
		return of(matrix, SimpleDenseMatrixOfDoubles.builder());
	}
	
	@Override public T get(int row, int column) {
		return matrix.get(column, row);
	}
	
	@Override public Matrix<T> get(Rectangle rect) {
		MatrixBuilder<T> b = getResultBuilder();
		return of(SimpleSubMatrixView.of(matrix, SimpleRectangle.create(
				rect.getLeftColumn(), rect.getTopRow(),
				rect.getColumnCount(), rect.getRowCount()), b), b);
	}
	
	@Override public int getRowCount() {
		return matrix.getColumnCount();
	}
	
	@Override public int getColumnCount() {
		return matrix.getRowCount();
	}
	
	@Override public Matrix<T> transpose() {
		return matrix;
	}
	
	@Override public T determinant() {
		return matrix.determinant();
	}
}
