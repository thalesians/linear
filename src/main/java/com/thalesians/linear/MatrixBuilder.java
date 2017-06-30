package com.thalesians.linear;

public interface MatrixBuilder<T> {
	T get(int row, int column);
	MatrixBuilder<T> setShape(int rowcount, int columncount);
	MatrixBuilder<T> setShape(Matrix<?> matrix);
	MatrixBuilder<T> setToIdentity();
	MatrixBuilder<T> set(int row, int column, T value);
	MatrixBuilder<T> set(int toprow, int leftcolumn, Matrix<? extends T> matrix);
	MatrixBuilder<T> set(Matrix<? extends T> matrix);
	MatrixBuilder<T> setAll(T value);
	MatrixBuilder<T> setAll(Rectangle rect, T value);
	MatrixBuilder<T> scale(int row, int column, T scalar);
	MatrixBuilder<T> scale(T scalar);
	MatrixBuilder<T> add(int row, int column, T value);
	MatrixBuilder<T> add(Matrix<? extends T> matrix);
	MatrixBuilder<T> addToDiagonal(Matrix<? extends T> vector);
	MatrixBuilder<T> subtract(Matrix<? extends T> matrix);
	MatrixBuilder<T> mult(Matrix<? extends T> leftmatrix, Matrix<? extends T> rightmatrix);
	MatrixBuilder<T> multByDiag(Matrix<? extends T> diag);
	MatrixBuilder<T> multElementwise(Matrix<? extends T> matrix);
	MatrixBuilder<T> invert();
	Matrix<T> build();
}
