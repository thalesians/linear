package com.thalesians.linear;

import org.ejml.alg.dense.decomposition.chol.CholeskyDecompositionInner_D64;
import org.ejml.alg.dense.decomposition.qr.QRDecompositionHouseholderColumn_D64;
import org.ejml.alg.dense.decomposition.svd.SafeSvd;
import org.ejml.alg.dense.decomposition.svd.SvdImplicitQrDecompose_D64;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.CholeskyDecomposition;
import org.ejml.interfaces.decomposition.EigenDecomposition;
import org.ejml.interfaces.decomposition.QRDecomposition;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;
import org.ejml.ops.EigenOps;

public class MatrixDecomposition {
	public final static class Cholesky {
		public final Matrix<Double> L;
		
		private Cholesky(Matrix<Double> l) {
			L = l;
		}
	}
	
	public static Cholesky cholesky(Matrix<Double> A) throws LinearException {
		DenseMatrix64F dA = MatrixUtils.toEJMLDenseMatrix64F(A);
		CholeskyDecomposition<DenseMatrix64F> choleskyDecomposer = new CholeskyDecompositionInner_D64(true);
		if (!choleskyDecomposer.decompose(dA)) {
			throw new LinearException("Unable to calculate Cholesky decomposition");
		}
		Matrix<Double> L = SimpleDenseMatrixOfDoubles.copyOf(choleskyDecomposer.getT(null));
		return new Cholesky(L);
	}
	
	/**
	 * Represents a QR decomposition of a double valued matrix
	 */
	public final static class QR {
		public final Matrix<Double> Q, R;
		
		private QR(Matrix<Double> q, Matrix<Double> r) {
			Q = q; R = r;
		}
	}

	/**
	 * Generates a QR decomposition for a double valued matrix i.e. A=QR, with
	 * Q orthogonal and R upper triangular
	 */
	public static QR qr(Matrix<Double> A) throws LinearException {
		DenseMatrix64F dA = MatrixUtils.toEJMLDenseMatrix64F(A);
		QRDecomposition<DenseMatrix64F> qrDecomposer = new QRDecompositionHouseholderColumn_D64();
		if (!qrDecomposer.decompose(dA)) {
			throw new LinearException("Unable to calculate QR decomposition");
		}
		Matrix<Double> Q = SimpleDenseMatrixOfDoubles.copyOf(qrDecomposer.getQ(null, false));
		Matrix<Double> R = SimpleDenseMatrixOfDoubles.copyOf(qrDecomposer.getR(null, false));
		return new QR(Q, R);
	}

	/**
	 * Represents a SVD (singular value decomposition) of a double valued matrix
	 * i.e. A = UDV^T, where U, V are orthogonal and D is diagonal.
	 */
	public final static class SVD {
		public final Matrix<Double> U, D, V;
		
		private SVD(Matrix<Double> u, Matrix<Double> d, Matrix<Double> v) {
			U = u;
			D = d; 
			V = v;
		}
	}

	/**
	 * Generates a SVD (singular value decomposition) of a double valued matrix
	 * i.e. A = UDV^T, where U, V are orthogonal and D is diagonal
	 */
	public static SVD svd(Matrix<Double> A) throws LinearException {
		DenseMatrix64F dA = MatrixUtils.toEJMLDenseMatrix64F(A);
		SingularValueDecomposition<DenseMatrix64F> svDecomposer =
				new SafeSvd(new SvdImplicitQrDecompose_D64(false, true, true, false));
		if (!svDecomposer.decompose(dA)) {
			throw new LinearException("Unable to calculate SVD decomposition");
		}
		Matrix<Double> U = SimpleDenseMatrixOfDoubles.copyOf(svDecomposer.getU(null, false));
		Matrix<Double> V = SimpleDenseMatrixOfDoubles.copyOf(svDecomposer.getV(null, false));
		Matrix<Double> D = SimpleDenseMatrixOfDoubles.copyOf(svDecomposer.getW(null));
		
		return new SVD(U, D, V);
	}
	
	public final static class ED {
		public final Matrix<Double> Q, Lambda;
		
		private ED(Matrix<Double> q, Matrix<Double> lambda) {
			Q = q;
			Lambda = lambda;
		}
	}
	
	public static ED ed(Matrix<Double> A) throws LinearException {
		DenseMatrix64F dA = MatrixUtils.toEJMLDenseMatrix64F(A);
		EigenDecomposition<DenseMatrix64F> eigDecomposer = DecompositionFactory.eig(A.getRowCount(), true);
		if (!eigDecomposer.decompose(dA)) {
			throw new LinearException("Unable to compute eigendecomposition");
		}
		Matrix<Double> Lambda = SimpleDenseMatrixOfDoubles.copyOf(EigenOps.createMatrixD(eigDecomposer));
		Matrix<Double> Q = SimpleDenseMatrixOfDoubles.copyOf(EigenOps.createMatrixV(eigDecomposer));
		return new ED(Q, Lambda);
	}
}
