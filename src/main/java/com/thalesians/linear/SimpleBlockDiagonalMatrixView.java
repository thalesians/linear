package com.thalesians.linear;

import com.google.common.collect.ImmutableList;

public final class SimpleBlockDiagonalMatrixView<T> extends AbstractMatrix<T> {	
	private final ImmutableList<Matrix<T>> blocks;
	private final T zero;
	private final int[] blockrowcounts;
	private final int[] blockcolumncounts;
	private final int rowcount;
	private final int columncount;
	private final int[] rowblockindices;
	private final int[] rowblockoffsets;
	private final int[] columnblockindices;
	private final int[] columnblockoffsets;
	
	private SimpleBlockDiagonalMatrixView(T zero, ImmutableList<Matrix<T>> blocks, MatrixBuilder<T> resultbuilder) {
		super(resultbuilder);
		
		this.blocks = blocks;
		
		this.zero = zero;
		
		this.blockrowcounts = new int[blocks.size()];
		this.blockcolumncounts = new int[blocks.size()];
		
		int trc = 0, tcc = 0;
		for (int i = 0; i < blocks.size(); ++i) {
			trc += this.blockrowcounts[i] = blocks.get(i).getRowCount();
			tcc += this.blockcolumncounts[i] = blocks.get(i).getColumnCount();
		}
		
		this.rowcount = trc; this.columncount = tcc;
		
		this.rowblockindices = new int[trc];
		this.rowblockoffsets = new int[trc];
		this.columnblockindices = new int[tcc];
		this.columnblockoffsets = new int[tcc];
		
		for (int ii = 0, i = 0; i < this.blockrowcounts.length; ++i) {
			for (int j = 0; j < this.blockrowcounts[i]; ++j, ++ii) {
				this.rowblockindices[ii] = i;
				this.rowblockoffsets[ii] = j;
			}
		}
		
		for (int ii = 0, i = 0; i < this.blockcolumncounts.length; ++i) {
			for (int j = 0; j < this.blockcolumncounts[i]; ++j, ++ii) {
				this.columnblockindices[ii] = i;
				this.columnblockoffsets[ii] = j;
			}
		}
	}

	public static <E> SimpleBlockDiagonalMatrixView<E> create(E zero, ImmutableList<Matrix<E>> blocks, MatrixBuilder<E> resultbuilder) {
		return new SimpleBlockDiagonalMatrixView<E>(zero, blocks, resultbuilder);
	}
	
	public static SimpleBlockDiagonalMatrixView<Double> create(ImmutableList<Matrix<Double>> blocks) {
		return create(0.0, blocks, SimpleDenseMatrixOfDoubles.builder());
	}
	
	@SafeVarargs
	public static SimpleBlockDiagonalMatrixView<Double> create(Matrix<Double>... blocks) {
		return create(ImmutableList.copyOf(blocks));
	}
	
	@Override public T get(int row, int column) {
		int rowBlockIndex = rowblockindices[row];
		int colBlockIndex = columnblockindices[column];
		if (rowBlockIndex != colBlockIndex) {
			return zero;
		} else {
			return blocks.get(rowBlockIndex).get(rowblockoffsets[row], columnblockoffsets[column]);
		}
	}
	
	@Override public int getRowCount() {
		return rowcount;
	}
	
	@Override public int getColumnCount() {
		return columncount;
	}
	
	@Override public Matrix<T> scale(T factor) {
		ImmutableList.Builder<Matrix<T>> bs = ImmutableList.builder();
		for (Matrix<T> block : blocks) {
			bs.add(block.scale(factor));
		}
		return create(zero, bs.build(), getResultBuilder());
	}
}
