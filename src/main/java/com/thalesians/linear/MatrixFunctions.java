package com.thalesians.linear;

import org.ejml.alg.dense.decomposition.chol.CholeskyDecompositionInner_D64;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.interfaces.decomposition.CholeskyDecomposition;
import org.ejml.interfaces.linsol.LinearSolver;

import com.google.common.base.Function;

public class MatrixFunctions {
	public static Function<Matrix<Double>, Matrix<Double>> choleskyDecompositionFunction() {
		return new CholeskyDecompositionFunction();
	}
	
	private static final class CholeskyDecompositionFunction implements Function<Matrix<Double>, Matrix<Double>> {
		@Override
		public Matrix<Double> apply(Matrix<Double> input) {
			CholeskyDecomposition<DenseMatrix64F> cd = new CholeskyDecompositionInner_D64();
			cd.decompose(MatrixUtils.toEJMLDenseMatrix64F(input).copy());
			return SimpleDenseMatrixOfDoubles.copyOf(cd.getT(null));
		}
	}
	
	public static Function<Matrix<Double>, Matrix<Double>> multiplyOnLeftFunction(Matrix<Double> multiplier) {
		return new MultiplyOnLeftFunction(multiplier);
	}
	
	private static final class MultiplyOnLeftFunction implements Function<Matrix<Double>, Matrix<Double>> {
		private final Matrix<Double> multiplier;

		public MultiplyOnLeftFunction(Matrix<Double> multiplier) {
			this.multiplier = multiplier;
		}
		
		@Override
		public Matrix<Double> apply(Matrix<Double> input) {
			return multiplier.mult(input);
		}
	}
	
	public static Function<Matrix<Double>, Matrix<Double>> multiplyOnRightFunction(Matrix<Double> multiplier) {
		return new MultiplyOnRightFunction(multiplier);
	}
	
	private static final class MultiplyOnRightFunction implements Function<Matrix<Double>, Matrix<Double>> {
		private final Matrix<Double> multiplier;
		
		public MultiplyOnRightFunction(Matrix<Double> multiplier) {
			this.multiplier = multiplier;
		}
		
		@Override
		public Matrix<Double> apply(Matrix<Double> input) {
			return input.mult(multiplier);
		}
	}
	
	public static final Matrix<Double> solve(Matrix<Double> A, Matrix<Double> b) {
		DenseMatrix64F A1 = MatrixUtils.toEJMLDenseMatrix64F(A);
		DenseMatrix64F b1 = MatrixUtils.toEJMLDenseMatrix64F(b);
		DenseMatrix64F x1 = new DenseMatrix64F(A.getColumnCount(), b.getColumnCount());
		
		LinearSolver<DenseMatrix64F> solver = (A1.getNumRows() == A1.getNumCols())
				? LinearSolverFactory.linear(A1.getNumRows())
				: LinearSolverFactory.leastSquaresQrPivot(false, false);
		solver.setA(A1);
		solver.solve(b1, x1);
		return new SimpleDenseMatrixOfDoubles(x1);
	}
}
