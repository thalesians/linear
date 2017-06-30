package com.thalesians.linear;

import com.google.common.base.Optional;

public class MatrixClosenessCheck<T> {
	private final ElementClosenessCheck<T> elementclosenesscheck;
	
	public MatrixClosenessCheck(ElementClosenessCheck<T> elementclosenesscheck) {
		this.elementclosenesscheck = elementclosenesscheck;
	}
	
	public static interface ElementClosenessCheck<T> {
		public boolean isClose(T lhs, T rhs);
	}
	
	public static class DoubleAbsoluteClosenessCheck implements ElementClosenessCheck<Double> {
		private final double tolerance;
		
		public DoubleAbsoluteClosenessCheck(double tolerance) {
			this.tolerance = tolerance;
		}
		
		public DoubleAbsoluteClosenessCheck() {
			this(ArithmeticsUtils.getMachineEpsilonDouble());
		}
		
		@Override public boolean isClose(Double lhs, Double rhs) {
			return Double.compare(lhs, rhs) == 0 || Math.abs(lhs - rhs) <= this.tolerance;
		}
		
		@Override public String toString() {
			return "absolute closeness check; tolerance = " + this.tolerance;
		}
	}
	
	public static enum ResultKind { CLOSENESS, ROW_COUNT_MISMATCH, COLUMN_COUNT_MISMATCH, MATRIX_VALUES_DISTANT }
	
	
	public static class Result<ValueType> {
		private final ResultKind resultkind;
		private final Optional<Integer> lhscount;
		private final Optional<Integer> rhscount;
		private final Optional<Integer> mismatchrowindex;
		private final Optional<Integer> mismatchcolumnindex;
		private final Optional<ValueType> lhselement;
		private final Optional<ValueType> rhselement;
		private final Optional<ElementClosenessCheck<ValueType>> elementclosenesscheck;
		
		private Result(
				ResultKind resultkind,
				Optional<Integer> lhscount,
				Optional<Integer> rhscount,
				Optional<Integer> mismatchrowindex,
				Optional<Integer> mismatchcolumnindex,
				Optional<ValueType> lhselement,
				Optional<ValueType> rhselement,
				Optional<ElementClosenessCheck<ValueType>> elementclosenesscheck
		) {
			this.resultkind = resultkind;
			this.lhscount = lhscount;
			this.rhscount = rhscount;
			this.mismatchrowindex = mismatchrowindex;
			this.mismatchcolumnindex = mismatchcolumnindex;
			this.lhselement = lhselement;
			this.rhselement = rhselement;
			this.elementclosenesscheck = elementclosenesscheck;			
		}
		
		public boolean isClose() {
			return resultkind == ResultKind.CLOSENESS;
		}
		
		public ResultKind getResultKind() {
			return resultkind;
		}
		
		public static <ValueType> Result<ValueType> makeClose(ElementClosenessCheck<ValueType> check) {
			return new Result<ValueType>(
					ResultKind.CLOSENESS,
					Optional.<Integer>absent(),
					Optional.<Integer>absent(),
					Optional.<Integer>absent(),
					Optional.<Integer>absent(),
					Optional.<ValueType>absent(),
					Optional.<ValueType>absent(),
					Optional.of(check));
		}

		public static <ValueType> Result<ValueType> makeRowCountMismatch(int lhsrowcount, int rhsrowcount) {
			return new Result<ValueType>(
					ResultKind.ROW_COUNT_MISMATCH,
					Optional.of(lhsrowcount),
					Optional.of(rhsrowcount),
					Optional.<Integer>absent(),
					Optional.<Integer>absent(),
					Optional.<ValueType>absent(),
					Optional.<ValueType>absent(),
					Optional.<ElementClosenessCheck<ValueType>>absent());
		}

		public static <ValueType> Result<ValueType> makeColumnCountMismatch(int lhscolumncount, int rhscolumncount) {
			return new Result<ValueType>(
					ResultKind.COLUMN_COUNT_MISMATCH,
					Optional.of(lhscolumncount),
					Optional.of(rhscolumncount),
					Optional.<Integer>absent(),
					Optional.<Integer>absent(),
					Optional.<ValueType>absent(),
					Optional.<ValueType>absent(),
					Optional.<ElementClosenessCheck<ValueType>>absent());
		}

		public static <ValueType> Result<ValueType> makeElementValuesDistant(int row, int column, ValueType lhselement, ValueType rhselement,
				ElementClosenessCheck<ValueType> check) {
			return new Result<ValueType>(
					ResultKind.MATRIX_VALUES_DISTANT,
					Optional.<Integer>absent(),
					Optional.<Integer>absent(),
					Optional.of(row),
					Optional.of(column),
					Optional.of(lhselement),
					Optional.of(rhselement),
					Optional.of(check));
		}
		
		public Optional<Integer> getLhsCount() {
			return lhscount;
		}
		
		public Optional<Integer> getRhsCount() {
			return rhscount;
		}
		
		public Optional<Integer> getMismatchRowIndex() {
			return mismatchrowindex;
		}

		public Optional<Integer> getMismatchColumnIndex() {
			return mismatchcolumnindex;
		}
		
		public Optional<ValueType> getLhs() {
			return lhselement;
		}
		
		public Optional<ValueType> getRhs() {
			return rhselement;
		}
		
		public Optional<ElementClosenessCheck<ValueType>> getCheck() {
			return elementclosenesscheck;
		}
		
		@Override public String toString() {
			StringBuilder sb = new StringBuilder();
			switch (resultkind) {
			case CLOSENESS:
				sb.append("Closeness check passed; element check = ").append(elementclosenesscheck.get());
				break;
			case ROW_COUNT_MISMATCH:
				sb.append("Row count mismatch; lhs row count = ").append(lhscount.get()).append(", rhs row count = ").append(rhscount.get());
				break;
			case COLUMN_COUNT_MISMATCH:
				sb.append("Column count mismatch; lhs column count = ").append(lhscount.get()).append(", rhs column count = ").append(rhscount.get());
				break;
			case MATRIX_VALUES_DISTANT:
				sb.append("Matrix values distant; lhs element value = ").append(lhselement.get()).append(", rhs element value = ")
						.append(rhselement.get()).append(", row index = ").append(mismatchrowindex.get()).append(", column index = ")
						.append(mismatchcolumnindex.get()).append(", element check = ").append(elementclosenesscheck.get());
				break;
			default:
				throw new IllegalStateException("Unfamiliar result type");
			}
			return sb.toString();
		}
	}
	
	public void assertSatisfied(T lhs, T rhs) {
		if (!this.elementclosenesscheck.isClose(lhs, rhs)) {
			throw new AssertionError(new StringBuilder().append("Scalar values distant: lhs = ").append(lhs).append(", rhs = ").append(rhs)
					.append(", check = ").append(elementclosenesscheck).toString());
		}
	}
	
	public Result<T> apply(Matrix<T> lhs, Matrix<T> rhs) {
		if (lhs.getRowCount() != rhs.getRowCount()) {
			return Result.makeRowCountMismatch(lhs.getRowCount(), rhs.getRowCount());
		}
		if (lhs.getColumnCount() != rhs.getColumnCount()) {
			return Result.makeColumnCountMismatch(lhs.getColumnCount(), rhs.getColumnCount());
		}
		for (int i = 0; i < lhs.getRowCount(); ++i) {
			for (int j = 0; j < lhs.getColumnCount(); ++j) {
				if (!this.elementclosenesscheck.isClose(lhs.get(i, j), rhs.get(i, j))) {
					return Result.makeElementValuesDistant(i, j, lhs.get(i, j), rhs.get(i, j), this.elementclosenesscheck);
				}
			}
		}
		return Result.makeClose(this.elementclosenesscheck);
	}
	
	public boolean isSatisfied(Matrix<T> lhs, Matrix<T> rhs) {
		return apply(lhs, rhs).isClose();
	}
	
	public void assertSatisfied(Matrix<T> lhs, Matrix<T> rhs) {
		Result<T> result = apply(lhs, rhs);
		if (!result.isClose()) {
			throw new AssertionError(result.toString());
		}
	}
}
