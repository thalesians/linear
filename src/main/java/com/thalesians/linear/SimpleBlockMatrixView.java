package com.thalesians.linear;

import com.google.common.collect.ImmutableList;

public final class SimpleBlockMatrixView<T> extends AbstractMatrix<T> {
	private final Matrix<T> matrix;
	private final ImmutableList<ImmutableList<? extends Rectangle>> rects;
	private final int[] rectrowcounts;
	private final int[] rectcolumncounts;
	private final int rowcount;
	private final int columncount;
	private final int[] rectrowindices;
	private final int[] rectrowoffsets;
	private final int[] rectcolumnindices;
	private final int[] rectcolumnoffsets;
	
	private SimpleBlockMatrixView(Matrix<T> matrix, ImmutableList<ImmutableList<? extends Rectangle>> rects, MatrixBuilder<T> resultbuilder) {
		super(resultbuilder);
		
		this.matrix = matrix;
		this.rects = rects;
		
		this.rectrowcounts = new int[rects.size()];
		this.rectcolumncounts = new int[rects.isEmpty() ? 0 : rects.get(0).size()];
		
		int trc = 0, tcc = 0;
		if (!rects.isEmpty()) {
			for (int i = 0; i < rects.size(); ++i) {
				trc += this.rectrowcounts[i] = rects.get(i).get(0).getRowCount();
			}
			ImmutableList<? extends Rectangle> toprowofrects = rects.get(0);
			for (int j = 0; j < toprowofrects.size(); ++j) {
				tcc += this.rectcolumncounts[j] = toprowofrects.get(j).getColumnCount();
			}
		}
		
		this.rowcount = trc; this.columncount = tcc;
		
		this.rectrowindices = new int[trc];
		this.rectrowoffsets = new int[trc];
		this.rectcolumnindices = new int[tcc];
		this.rectcolumnoffsets = new int[tcc];

		for (int ii = 0, i = 0; i < this.rectrowcounts.length; ++i) {
			for (int j = 0; j < this.rectrowcounts[i]; ++j, ++ii) {
				this.rectrowindices[ii] = i;
				this.rectrowoffsets[ii] = j;
			}
		}
		for (int ii = 0, i = 0; i < this.rectcolumncounts.length; ++i) {
			for (int j = 0; j < this.rectcolumncounts[i]; ++j, ++ii) {
				this.rectcolumnindices[ii] = i;
				this.rectcolumnoffsets[ii] = j;
			}
		}
	}
	
	public static <E> SimpleBlockMatrixView<E> of(Matrix<E> matrix, ImmutableList<ImmutableList<? extends Rectangle>> rects, MatrixBuilder<E> builder) {
		return new SimpleBlockMatrixView<E>(matrix, rects, builder);
	}
	
	public static SimpleBlockMatrixView<Double> of(Matrix<Double> matrix, ImmutableList<ImmutableList<? extends Rectangle>> rects) {
		return of(matrix, rects, SimpleDenseMatrixOfDoubles.builder());
	}
	
	@Override public T get(int row, int column) {
		Rectangle rect = rects.get(rectrowindices[row]).get(rectcolumnindices[column]);
		return matrix.get(rect.getTopRow() + rectrowoffsets[row], rect.getLeftColumn() + rectcolumnoffsets[column]);
	}
	
	@Override public int getRowCount() {
		return rowcount;
	}
	
	@Override public int getColumnCount() {
		return columncount;
	}
}
