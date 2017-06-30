package com.thalesians.linear;

public interface Matrix<T> {
	T get(int row, int column);
	Matrix<T> get(Rectangle rect);
	int getRowCount();
	int getColumnCount();
	Matrix<T> scale(T scalar);
	Matrix<T> add(Matrix<T> matrix);
	Matrix<T> subtract(Matrix<T> matrix);
	Matrix<T> mult(Matrix<T> matrix);
	Matrix<T> multByDiag(Matrix<T> diag);
	Matrix<T> vec();
	Matrix<T> unvec(int rowcount);
	Matrix<T> kroneckerProduct(Matrix<T> matrix);
	Matrix<T> kroneckerSum(Matrix<T> matrix);
	Matrix<T> transpose();
	Matrix<T> invert();
	T determinant();
}
