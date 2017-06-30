package com.thalesians.linear;

import com.google.common.collect.ImmutableList;

public final class SimpleBlockMatrix<T, MatrixType extends Matrix<? extends T>> extends AbstractMatrix<T> {
	private final ImmutableList<ImmutableList<MatrixType>> blocks;
	private final int[] blockrowcounts;
	private final int[] blockcolumncounts;
	private final int[] blocktoprows;
	private final int[] blockleftcolumns;
	private final int rowcount;
	private final int columncount;
	private final int[] blockrowindices;
	private final int[] blockrowoffsets;
	private final int[] blockcolumnindices;
	private final int[] blockcolumnoffsets;
	
	private SimpleBlockMatrix(MatrixBuilder<T> resultbuilder, ImmutableList<ImmutableList<MatrixType>> blocks) {
		super(resultbuilder);
		
		this.blocks = blocks;
		
		this.blockrowcounts = new int[blocks.size()];
		this.blockcolumncounts = new int[blocks.isEmpty() ? 0 : blocks.get(0).size()];
		this.blocktoprows = new int[blocks.size()];
		this.blockleftcolumns = new int[blocks.isEmpty() ? 0 : blocks.get(0).size()];
		
		int trc = 0, tcc = 0;
		if (!blocks.isEmpty()) {
			for (int i = 0; i < blocks.size(); ++i) {
				this.blocktoprows[i] = trc;
				trc += this.blockrowcounts[i] = blocks.get(i).get(0).getRowCount();
			}
			ImmutableList<MatrixType> toprowofblocks = blocks.get(0);
			for (int j = 0; j < toprowofblocks.size(); ++j) {
				this.blockleftcolumns[j] = tcc;
				tcc += this.blockcolumncounts[j] = toprowofblocks.get(j).getColumnCount();
			}
		}
		
		this.rowcount = trc; this.columncount = tcc;
		
		this.blockrowindices = new int[trc];
		this.blockrowoffsets = new int[trc];
		this.blockcolumnindices = new int[tcc];
		this.blockcolumnoffsets = new int[tcc];
		
		for (int ii = 0, i = 0; i < this.blockrowcounts.length; ++i) {
			for (int j = 0; j < this.blockrowcounts[i]; ++j, ++ii) {
				this.blockrowindices[ii] = i;
				this.blockrowoffsets[ii] = j;
			}
		}
		
		for (int ii = 0, i = 0; i < this.blockcolumncounts.length; ++i) {
			for (int j = 0; j < this.blockcolumncounts[i]; ++j, ++ii) {
				this.blockcolumnindices[ii] = i;
				this.blockcolumnoffsets[ii] = j;
			}
		}
	}
	
	public static <VT, MT extends Matrix<? extends VT>> SimpleBlockMatrix<VT, MT> of(MatrixBuilder<VT> builder, ImmutableList<ImmutableList<MT>> blocks) {
		return new SimpleBlockMatrix<VT, MT>(builder, blocks);
	}
	
	@SafeVarargs
	public static <VT, MT extends Matrix<? extends VT>> SimpleBlockMatrix<VT, MT> of(MatrixBuilder<VT> builder, ImmutableList<MT>... blocks) {
		return new SimpleBlockMatrix<VT, MT>(builder, ImmutableList.copyOf(blocks));
	}
	
	public static <MT extends Matrix<Double>> SimpleBlockMatrix<Double, MT> of(ImmutableList<ImmutableList<MT>> blocks) {
		return of(SimpleDenseMatrixOfDoubles.builder(), blocks);
	}
	
	@SafeVarargs
	public static <MT extends Matrix<Double>> SimpleBlockMatrix<Double, MT> of(ImmutableList<MT>... blocks) {
		return of(ImmutableList.copyOf(blocks));
	}
	
	public static <VT, MT extends Matrix<? extends VT>> SimpleBlockMatrix<VT, MT> createRow(MatrixBuilder<VT> builder, ImmutableList<MT> blocks) {
		return of(builder, ImmutableList.of(blocks));
	}
	
	@SafeVarargs
	public static <VT, MT extends Matrix<? extends VT>> SimpleBlockMatrix<VT, MT> createRow(MatrixBuilder<VT> builder, MT... blocks) {
		return createRow(builder, ImmutableList.copyOf(blocks));
	}
	
	public static <MT extends Matrix<Double>> SimpleBlockMatrix<Double, MT> createRow(ImmutableList<MT> blocks) {
		return createRow(SimpleDenseMatrixOfDoubles.builder(), blocks);
	}
	
	@SafeVarargs
	public static <MT extends Matrix<Double>> SimpleBlockMatrix<Double, MT> createRow(MT... blocks) {
		return createRow(ImmutableList.copyOf(blocks));
	}
	
	public static <VT, MT extends Matrix<? extends VT>> SimpleBlockMatrix<VT, MT> createColumn(MatrixBuilder<VT> builder, ImmutableList<MT> blocks) {
		ImmutableList.Builder<ImmutableList<MT>> b = ImmutableList.builder();
		for (MT block : blocks) {
			b.add(ImmutableList.of(block));
		}
		return of(builder, b.build());
	}
	
	@SafeVarargs
	public static <VT, MT extends Matrix<? extends VT>> SimpleBlockMatrix<VT, MT> createColumn(MatrixBuilder<VT> builder, MT... blocks) {
		return createColumn(builder, ImmutableList.copyOf(blocks));
	}
	
	public static <MT extends Matrix<Double>> SimpleBlockMatrix<Double, MT> createColumn(ImmutableList<MT> blocks) {
		return createColumn(SimpleDenseMatrixOfDoubles.builder(), blocks);
	}
	
	@SafeVarargs
	public static <MT extends Matrix<Double>> SimpleBlockMatrix<Double, MT> createColumn(MT... blocks) {
		return createColumn(ImmutableList.copyOf(blocks));
	}
	
	@Override public T get(int row, int column) {
		int ri = blockrowindices[row], ci = blockcolumnindices[column];
		MatrixType block = blocks.get(ri).get(ci);
		return block.get(blockrowoffsets[row], blockcolumnoffsets[column]);
	}
	
	@Override public int getRowCount() {
		return rowcount;
	}
	
	@Override public int getColumnCount() {
		return columncount;
	}
}
