/*
 Copyright (C) 2009 Richard Gomes

 This source code is release under the BSD License.

 This file is part of JQuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://jquantlib.org/

 JQuantLib is free software: you can redistribute it and/or modify it
 under the terms of the JQuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <jquant-devel@lists.sourceforge.net>. The license is also available online at
 <http://www.jquantlib.org/index.php/LICENSE.TXT>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.

 JQuantLib is based on QuantLib. http://quantlib.org/
 When applicable, the original copyright notice follows this notice.
 */
package org.jquantlib.math.matrixutilities;

import org.jquantlib.QL;
import org.jquantlib.lang.annotation.QualityAssurance;
import org.jquantlib.lang.annotation.QualityAssurance.Quality;
import org.jquantlib.lang.annotation.QualityAssurance.Version;

/**
 * Cholesky Decomposition.
 * <P>
 * For a symmetric, positive definite matrix A, the Cholesky decomposition is an lower triangular matrix L so that A = L*L'.
 * <P>
 * If the matrix is not symmetric or positive definite, the constructor returns a partial decomposition and sets an internal flag
 * that may be queried by the isSPD() method.
 *
 * @Note: This class was adapted from JAMA
 * @see <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a>
 */
@QualityAssurance(quality = Quality.Q1_TRANSLATION, version = Version.OTHER, reviewers = { "Richard Gomes" })
public class CholeskyDecomposition extends Matrix {

    private final static String MATRIX_IS_NOT_SIMMETRIC_POSITIVE = "Matrix is not symmetric positive definite.";

    /** Matrix for internal storage of decomposition. */
    private final Matrix L;

    /** Symmetric and positive definite flag. */
    private boolean isspd;

    //
    // public constructors
    //

    /**
     * Cholesky algorithm for symmetric and positive definite matrix.
     *
     * @param A is a square, symmetric matrix.
     * @return Structure to access L and isspd flag.
     */
    public CholeskyDecomposition(final Matrix A) {
        super(A);
        QL.require(rows == cols, MATRIX_MUST_BE_SQUARE);

        L = new Matrix(rows, rows);
        isspd = (rows == cols);

        // Main loop.
        for (int j = 0; j < cols; j++) {
            double d = 0.0;
            for (int k = 0; k < j; k++) {
                double s = 0.0;
                for (int i = 0; i < k; i++) {
                    s += L.data[L.address(k, i)] * L.data[L.address(j, i)];
                }
                L.data[L.address(j, k)] = s = (this.data[this.address(j, k)] - s) / L.data[L.address(k, k)];
                d = d + s * s;
                isspd = isspd & (this.data[this.address(k, j)] == this.data[this.address(j, k)]);
            }
            d = this.data[this.address(j, j)] - d;
            isspd = isspd & (d > 0.0);
            L.data[L.address(j, j)] = Math.sqrt(Math.max(d, 0.0));
            for (int k = j + 1; k < cols; k++) {
                L.data[L.address(j, k)] = 0.0;
            }
        }
    }

    //
    // public methods
    //

    /**
     * Is the matrix symmetric and positive definite?
     *
     * @return true if A is symmetric and positive definite.
     */
    public boolean isSPD() {
        return isspd;
    }

    /**
     * Return triangular factor.
     *
     * @return L
     */
    public Matrix getL() {
        return L.clone();
    }

    /**
     * Solve A*X = B
     *
     * @param m a Matrix with as many rows as A and any number of columns.
     * @return X so that L*L'*X = B
     * @exception IllegalArgumentException Matrix row dimensions must agree.
     * @exception RuntimeException Matrix is not symmetric positive definite.
     */

    @Override
    public Matrix solve(final Matrix B) {
        QL.require(B.rows == this.rows, MATRIX_IS_INCOMPATIBLE);
        if (!this.isSPD())
            throw new RuntimeException(MATRIX_IS_NOT_SIMMETRIC_POSITIVE);

        // Copy right hand side.
        final int nx = B.cols;
        final Matrix X = B.clone();

        // Solve L*Y = B;
        for (int k = 0; k < cols; k++) {
            for (int j = 0; j < nx; j++) {
                for (int i = 0; i < k; i++) {
                    X.data[X.address(k, j)] -= X.data[X.address(i, j)] * L.data[L.address(k, i)];
                }
                X.data[X.address(k, j)] /= L.data[L.address(k, k)];
            }
        }

        // Solve L'*X = Y;
        for (int k = cols - 1; k >= 0; k--) {
            for (int j = 0; j < nx; j++) {
                for (int i = k + 1; i < cols; i++) {
                    X.data[X.address(k, j)] -= X.data[X.address(i, j)] * L.data[L.address(i, k)];
                }
                X.data[X.address(k, j)] /= L.data[L.address(k, k)];
            }
        }

        return X;
    }

}
