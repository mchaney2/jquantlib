/*
Copyright (C) 2008 Richard Gomes

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
 * Eigenvalues and eigenvectors of a real matrix.
 * <P>
 * If A is symmetric, then A = V*D*V' where the eigenvalue matrix D is diagonal and the eigenvector matrix V is orthogonal. I.e. A =
 * V.times(D.times(V.transpose())) and V.times(V.transpose()) equals the identity matrix.
 * <P>
 * If A is not symmetric, then the eigenvalue matrix D is block diagonal with the real eigenvalues in 1-by-1 blocks and any complex
 * eigenvalues, lambda + i*mu, in 2-by-2 blocks, [lambda, mu; -mu, lambda]. The columns of V represent the eigenvectors in the sense
 * that A*V = V*D, i.e. A.times(V) equals V.times(D). The matrix V may be badly conditioned, or even singular, so the validity of
 * the equation A = V*D*inverse(V) depends upon V.cond().
 *
 * @Note: This class was adapted from JAMA
 * @see <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a>
 *
 * @author Richard Gomes
 */
@QualityAssurance(quality = Quality.Q1_TRANSLATION, version = Version.OTHER, reviewers = { "Richard Gomes" })
public class EigenvalueDecomposition extends Matrix {

    /**
     * Symmetry flag.
     */
    private boolean issymmetric;

    /**
     * Arrays for internal storage of eigenvalues.
     */
    private final double[] d, e;

    /**
     * Array for internal storage of eigenvectors.
     */
    private final Matrix V;

    /**
     * Array for internal storage of nonsymmetric Hessenberg form.
     */
    private Matrix H;

    /**
     * Working storage for nonsymmetric algorithm.
     */
    private double[] ort;

    //
    // private transient fields
    //

    private transient double cdivr, cdivi;

    //
    // public constructors
    //

    /**
     * Check for symmetry, then construct the eigenvalue decomposition
     *
     * @param A is a square matrix
     * @return Structure to access D and V.
     */

    public EigenvalueDecomposition(final Matrix A) {
        super(A);
        QL.require(rows == cols, MATRIX_MUST_BE_SQUARE);

        V = new Matrix(cols, cols);
        d = new double[cols];
        e = new double[cols];

        issymmetric = true;
        for (int j = 0; (j < cols) & issymmetric; j++) {
            for (int i = 0; (i < cols) & issymmetric; i++) {
                issymmetric = (this.data[this.address(i, j)] == this.data[this.address(j, i)]);
            }
        }

        if (issymmetric) {
            for (int i = 0; i < cols; i++) {
                for (int j = 0; j < cols; j++) {
                    V.data[V.address(i, j)] = this.data[this.address(i, j)];
                }
            }

            // Tridiagonalize.
            tred2();

            // Diagonalize.
            tql2();

        } else {
            H = new Matrix(cols, cols);
            ort = new double[cols];

            for (int j = 0; j < cols; j++) {
                for (int i = 0; i < cols; i++) {
                    H.data[H.address(i, j)] = this.data[this.address(i, j)];
                }
            }

            // Reduce to Hessenberg form.
            orthes();

            // Reduce Hessenberg to real Schur form.
            hqr2();
        }
    }

    //
    // public methods
    //

    /**
     * Return the eigenvector matrix
     *
     * @return V
     */
    public Matrix getV() {
        return V.clone();
    }

    /**
     * Return the real parts of the eigenvalues
     *
     * @return real(diag(D))
     */
    public double[] getRealEigenvalues() {
        return d;
    }

    /**
     * Return the imaginary parts of the eigenvalues
     *
     * @return imag(diag(D))
     */
    public double[] getImagEigenvalues() {
        return e;
    }

    /**
     * Return the block diagonal eigenvalue matrix
     *
     * @return D
     */
    public Matrix getD() {
        final Matrix D = new Matrix(cols, cols);
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < cols; j++) {
                D.data[D.address(i, j)] = 0.0;
            }
            D.data[D.address(i, i)] = d[i];
            if (e[i] > 0) {
                D.data[D.address(i, i + 1)] = e[i];
            } else if (e[i] < 0) {
                D.data[D.address(i, i - 1)] = e[i];
            }
        }
        return D;
    }


    //
    // private methods
    //

    /**
     * Symmetric Householder reduction to tridiagonal form.
     */
    private void tred2() {

        // This is derived from the Algol procedures tred2 by
        // Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
        // Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
        // Fortran subroutine in EISPACK.

        for (int j = 0; j < cols; j++) {
            d[j] = V.data[V.address(cols - 1, j)];
        }

        // Householder reduction to tridiagonal form.

        for (int i = cols - 1; i > 0; i--) {

            // Scale to avoid under/overflow.

            double scale = 0.0;
            double h = 0.0;
            for (int k = 0; k < i; k++) {
                scale = scale + Math.abs(d[k]);
            }
            if (scale == 0.0) {
                e[i] = d[i - 1];
                for (int j = 0; j < i; j++) {
                    d[j] = V.data[V.address(i - 1, j)];
                    V.data[V.address(i, j)] = 0.0;
                    V.data[V.address(j, i)] = 0.0;
                }
            } else {

                // Generate Householder vector.

                for (int k = 0; k < i; k++) {
                    d[k] /= scale;
                    h += d[k] * d[k];
                }
                double f = d[i - 1];
                double g = Math.sqrt(h);
                if (f > 0) {
                    g = -g;
                }
                e[i] = scale * g;
                h = h - f * g;
                d[i - 1] = f - g;
                for (int j = 0; j < i; j++) {
                    e[j] = 0.0;
                }

                // Apply similarity transformation to remaining columns.

                for (int j = 0; j < i; j++) {
                    f = d[j];
                    V.data[V.address(j, i)] = f;
                    g = e[j] + V.data[V.address(j, j)] * f;
                    for (int k = j + 1; k <= i - 1; k++) {
                        g += V.data[V.address(k, j)] * d[k];
                        e[k] += V.data[V.address(k, j)] * f;
                    }
                    e[j] = g;
                }
                f = 0.0;
                for (int j = 0; j < i; j++) {
                    e[j] /= h;
                    f += e[j] * d[j];
                }
                final double hh = f / (h + h);
                for (int j = 0; j < i; j++) {
                    e[j] -= hh * d[j];
                }
                for (int j = 0; j < i; j++) {
                    f = d[j];
                    g = e[j];
                    for (int k = j; k <= i - 1; k++) {
                        V.data[V.address(k, j)] -= (f * e[k] + g * d[k]);
                    }
                    d[j] = V.data[V.address(i - 1, j)];
                    V.data[V.address(i, j)] = 0.0;
                }
            }
            d[i] = h;
        }

        // Accumulate transformations.

        for (int i = 0; i < cols - 1; i++) {
            V.data[V.address(cols - 1, i)] = V.data[V.address(i, i)];
            V.data[V.address(i, i)] = 1.0;
            final double h = d[i + 1];
            if (h != 0.0) {
                for (int k = 0; k <= i; k++) {
                    d[k] = V.data[V.address(k, i + 1)] / h;
                }
                for (int j = 0; j <= i; j++) {
                    double g = 0.0;
                    for (int k = 0; k <= i; k++) {
                        g += V.data[V.address(k, i + 1)] * V.data[V.address(k, j)];
                    }
                    for (int k = 0; k <= i; k++) {
                        V.data[V.address(k, j)] -= g * d[k];
                    }
                }
            }
            for (int k = 0; k <= i; k++) {
                V.data[V.address(k, i + 1)] = 0.0;
            }
        }
        for (int j = 0; j < cols; j++) {
            d[j] = V.data[V.address(cols - 1, j)];
            V.data[V.address(cols - 1, j)] = 0.0;
        }
        V.data[V.address(cols - 1, cols - 1)] = 1.0;
        e[0] = 0.0;
    }

    /**
     * Symmetric tridiagonal QL algorithm.
     */
    private void tql2() {

        // This is derived from the Algol procedures tql2, by
        // Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
        // Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
        // Fortran subroutine in EISPACK.

        for (int i = 1; i < cols; i++) {
            e[i - 1] = e[i];
        }
        e[cols - 1] = 0.0;

        double f = 0.0;
        double tst1 = 0.0;
        final double eps = Math.pow(2.0, -52.0);
        for (int l = 0; l < cols; l++) {

            // Find small subdiagonal element

            tst1 = Math.max(tst1, Math.abs(d[l]) + Math.abs(e[l]));
            int m = l;
            while (m < cols) {
                if (Math.abs(e[m]) <= eps * tst1) {
                    break;
                }
                m++;
            }

            // If m == l, d[l] is an eigenvalue,
            // otherwise, iterate.

            if (m > l) {
                int iter = 0;
                do {
                    iter = iter + 1; // (Could check iteration count here.)

                    // Compute implicit shift

                    double g = d[l];
                    double p = (d[l + 1] - g) / (2.0 * e[l]);
                    double r = hypot(p, 1.0);
                    if (p < 0) {
                        r = -r;
                    }
                    d[l] = e[l] / (p + r);
                    d[l + 1] = e[l] * (p + r);
                    final double dl1 = d[l + 1];
                    double h = g - d[l];
                    for (int i = l + 2; i < cols; i++) {
                        d[i] -= h;
                    }
                    f = f + h;

                    // Implicit QL transformation.

                    p = d[m];
                    double c = 1.0;
                    double c2 = c;
                    double c3 = c;
                    final double el1 = e[l + 1];
                    double s = 0.0;
                    double s2 = 0.0;
                    for (int i = m - 1; i >= l; i--) {
                        c3 = c2;
                        c2 = c;
                        s2 = s;
                        g = c * e[i];
                        h = c * p;
                        r = hypot(p, e[i]);
                        e[i + 1] = s * r;
                        s = e[i] / r;
                        c = p / r;
                        p = c * d[i] - s * g;
                        d[i + 1] = h + s * (c * g + s * d[i]);

                        // Accumulate transformation.

                        for (int k = 0; k < cols; k++) {
                            h = V.data[V.address(k, i + 1)];
                            V.data[V.address(k, i + 1)] = s * V.data[V.address(k, i)] + c * h;
                            V.data[V.address(k, i)] = c * V.data[V.address(k, i)] - s * h;
                        }
                    }
                    p = -s * s2 * c3 * el1 * e[l] / dl1;
                    e[l] = s * p;
                    d[l] = c * p;

                    // Check for convergence.

                } while (Math.abs(e[l]) > eps * tst1);
            }
            d[l] = d[l] + f;
            e[l] = 0.0;
        }

        // Sort eigenvalues and corresponding vectors.

        for (int i = 0; i < cols - 1; i++) {
            int k = i;
            double p = d[i];
            for (int j = i + 1; j < cols; j++) {
                if (d[j] < p) {
                    k = j;
                    p = d[j];
                }
            }
            if (k != i) {
                d[k] = d[i];
                d[i] = p;
                for (int j = 0; j < cols; j++) {
                    p = V.data[V.address(j, i)];
                    V.data[V.address(j, i)] = V.data[V.address(j, k)];
                    V.data[V.address(j, k)] = p;
                }
            }
        }
    }

    /**
     * Nonsymmetric reduction to Hessenberg form.
     */
    private void orthes() {

        // This is derived from the Algol procedures orthes and ortran,
        // by Martin and Wilkinson, Handbook for Auto. Comp.,
        // Vol.ii-Linear Algebra, and the corresponding
        // Fortran subroutines in EISPACK.

        final int low = 0;
        final int high = cols - 1;

        for (int m = low + 1; m <= high - 1; m++) {

            // Scale column.

            double scale = 0.0;
            for (int i = m; i <= high; i++) {
                scale = scale + Math.abs(H.data[H.address(i, m - 1)]);
            }
            if (scale != 0.0) {

                // Compute Householder transformation.

                double h = 0.0;
                for (int i = high; i >= m; i--) {
                    ort[i] = H.data[H.address(i, m - 1)] / scale;
                    h += ort[i] * ort[i];
                }
                double g = Math.sqrt(h);
                if (ort[m] > 0) {
                    g = -g;
                }
                h = h - ort[m] * g;
                ort[m] = ort[m] - g;

                // Apply Householder similarity transformation
                // H = (I-u*u'/h)*H*(I-u*u')/h)

                for (int j = m; j < cols; j++) {
                    double f = 0.0;
                    for (int i = high; i >= m; i--) {
                        f += ort[i] * H.data[H.address(i, j)];
                    }
                    f = f / h;
                    for (int i = m; i <= high; i++) {
                        H.data[H.address(i, j)] -= f * ort[i];
                    }
                }

                for (int i = 0; i <= high; i++) {
                    double f = 0.0;
                    for (int j = high; j >= m; j--) {
                        f += ort[j] * H.data[H.address(i, j)];
                    }
                    f = f / h;
                    for (int j = m; j <= high; j++) {
                        H.data[H.address(i, j)] -= f * ort[j];
                    }
                }
                ort[m] = scale * ort[m];
                H.data[H.address(m, m - 1)] = scale * g;
            }
        }

        // Accumulate transformations (Algol's ortran).

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < cols; j++) {
                V.data[V.address(i, j)] = (i == j ? 1.0 : 0.0);
            }
        }

        for (int m = high - 1; m >= low + 1; m--) {
            if (H.data[H.address(m, m - 1)] != 0.0) {
                for (int i = m + 1; i <= high; i++) {
                    ort[i] = H.data[H.address(i, m - 1)];
                }
                for (int j = m; j <= high; j++) {
                    double g = 0.0;
                    for (int i = m; i <= high; i++) {
                        g += ort[i] * V.data[V.address(i, j)];
                    }
                    // Double division avoids possible underflow
                    g = (g / ort[m]) / H.data[H.address(m, m - 1)];
                    for (int i = m; i <= high; i++) {
                        V.data[V.address(i, j)] += g * ort[i];
                    }
                }
            }
        }
    }

    /**
     * Complex scalar division.
     *
     * @param xr
     * @param xi
     * @param yr
     * @param yi
     */
    private void cdiv(final double xr, final double xi, final double yr, final double yi) {
        double r, d;
        if (Math.abs(yr) > Math.abs(yi)) {
            r = yi / yr;
            d = yr + r * yi;
            cdivr = (xr + r * xi) / d;
            cdivi = (xi - r * xr) / d;
        } else {
            r = yr / yi;
            d = yi + r * yr;
            cdivr = (r * xr + xi) / d;
            cdivi = (r * xi - xr) / d;
        }
    }

    // Nonsymmetric reduction from Hessenberg to real Schur form.

    private void hqr2() {

        // This is derived from the Algol procedure hqr2,
        // by Martin and Wilkinson, Handbook for Auto. Comp.,
        // Vol.ii-Linear Algebra, and the corresponding
        // Fortran subroutine in EISPACK.

        // Initialize

        final int nn = this.cols;
        int n = nn - 1;
        final int low = 0;
        final int high = nn - 1;
        final double eps = Math.pow(2.0, -52.0);
        double exshift = 0.0;
        double p = 0, q = 0, r = 0, s = 0, z = 0, t, w, x, y;

        // Store roots isolated by balanc and compute matrix norm

        double norm = 0.0;
        for (int i = 0; i < nn; i++) {
            if (i < low | i > high) {
                d[i] = H.data[H.address(i, i)];
                e[i] = 0.0;
            }
            for (int j = Math.max(i - 1, 0); j < nn; j++) {
                norm = norm + Math.abs(H.data[H.address(i, j)]);
            }
        }

        // Outer loop over eigenvalue index

        int iter = 0;
        while (n >= low) {

            // Look for single small sub-diagonal element

            int l = n;
            while (l > low) {
                s = Math.abs(H.data[H.address(l - 1, l - 1)]) + Math.abs(H.data[H.address(l, l)]);
                if (s == 0.0) {
                    s = norm;
                }
                if (Math.abs(H.data[H.address(l, l - 1)]) < eps * s) {
                    break;
                }
                l--;
            }

            // Check for convergence
            // One root found

            if (l == n) {
                H.data[H.address(n, n)] = H.data[H.address(n, n)] + exshift;
                d[n] = H.data[H.address(n, n)];
                e[n] = 0.0;
                n--;
                iter = 0;

                // Two roots found

            } else if (l == n - 1) {
                w = H.data[H.address(n, n - 1)] * H.data[H.address(n - 1, n)];
                p = (H.data[H.address(n - 1, n - 1)] - H.data[H.address(n, n)]) / 2.0;
                q = p * p + w;
                z = Math.sqrt(Math.abs(q));
                H.data[H.address(n, n)] = H.data[H.address(n, n)] + exshift;
                H.data[H.address(n - 1, n - 1)] = H.data[H.address(n - 1, n - 1)] + exshift;
                x = H.data[H.address(n, n)];

                // Real pair

                if (q >= 0) {
                    if (p >= 0) {
                        z = p + z;
                    } else {
                        z = p - z;
                    }
                    d[n - 1] = x + z;
                    d[n] = d[n - 1];
                    if (z != 0.0) {
                        d[n] = x - w / z;
                    }
                    e[n - 1] = 0.0;
                    e[n] = 0.0;
                    x = H.data[H.address(n, n - 1)];
                    s = Math.abs(x) + Math.abs(z);
                    p = x / s;
                    q = z / s;
                    r = Math.sqrt(p * p + q * q);
                    p = p / r;
                    q = q / r;

                    // Row modification

                    for (int j = n - 1; j < nn; j++) {
                        z = H.data[H.address(n - 1, j)];
                        H.data[H.address(n - 1, j)] = q * z + p * H.data[H.address(n, j)];
                        H.data[H.address(n, j)] = q * H.data[H.address(n, j)] - p * z;
                    }

                    // Column modification

                    for (int i = 0; i <= n; i++) {
                        z = H.data[H.address(i, n - 1)];
                        H.data[H.address(i, n - 1)] = q * z + p * H.data[H.address(i, n)];
                        H.data[H.address(i, n)] = q * H.data[H.address(i, n)] - p * z;
                    }

                    // Accumulate transformations

                    for (int i = low; i <= high; i++) {
                        z = V.data[V.address(i, n - 1)];
                        V.data[V.address(i, n - 1)] = q * z + p * V.data[V.address(i, n)];
                        V.data[V.address(i, n)] = q * V.data[V.address(i, n)] - p * z;
                    }

                    // Complex pair

                } else {
                    d[n - 1] = x + p;
                    d[n] = x + p;
                    e[n - 1] = z;
                    e[n] = -z;
                }
                n = n - 2;
                iter = 0;

                // No convergence yet

            } else {

                // Form shift

                x = H.data[H.address(n, n)];
                y = 0.0;
                w = 0.0;
                if (l < n) {
                    y = H.data[H.address(n - 1, n - 1)];
                    w = H.data[H.address(n, n - 1)] * H.data[H.address(n - 1, n)];
                }

                // Wilkinson's original ad hoc shift

                if (iter == 10) {
                    exshift += x;
                    for (int i = low; i <= n; i++) {
                        H.data[H.address(i, i)] -= x;
                    }
                    s = Math.abs(H.data[H.address(n, n - 1)]) + Math.abs(H.data[H.address(n - 1, n - 2)]);
                    x = y = 0.75 * s;
                    w = -0.4375 * s * s;
                }

                // MATLAB's new ad hoc shift

                if (iter == 30) {
                    s = (y - x) / 2.0;
                    s = s * s + w;
                    if (s > 0) {
                        s = Math.sqrt(s);
                        if (y < x) {
                            s = -s;
                        }
                        s = x - w / ((y - x) / 2.0 + s);
                        for (int i = low; i <= n; i++) {
                            H.data[H.address(i, i)] -= s;
                        }
                        exshift += s;
                        x = y = w = 0.964;
                    }
                }

                iter = iter + 1; // (Could check iteration count here.)

                // Look for two consecutive small sub-diagonal elements

                int m = n - 2;
                while (m >= l) {
                    z = H.data[H.address(m, m)];
                    r = x - z;
                    s = y - z;
                    p = (r * s - w) / H.data[H.address(m + 1, m)] + H.data[H.address(m, m + 1)];
                    q = H.data[H.address(m + 1, m + 1)] - z - r - s;
                    r = H.data[H.address(m + 2, m + 1)];
                    s = Math.abs(p) + Math.abs(q) + Math.abs(r);
                    p = p / s;
                    q = q / s;
                    r = r / s;
                    if (m == l) {
                        break;
                    }
                    if (Math.abs(H.data[H.address(m, m - 1)]) * (Math.abs(q) + Math.abs(r)) < eps
                            * (Math.abs(p) * (Math.abs(H.data[H.address(m - 1, m - 1)]) + Math.abs(z) + Math.abs(H.data[H.address(
                                    m + 1, m + 1)])))) {
                        break;
                    }
                    m--;
                }

                for (int i = m + 2; i <= n; i++) {
                    H.data[H.address(i, i - 2)] = 0.0;
                    if (i > m + 2) {
                        H.data[H.address(i, i - 3)] = 0.0;
                    }
                }

                // Double QR step involving rows l:n and columns m:n

                for (int k = m; k <= n - 1; k++) {
                    final boolean notlast = (k != n - 1);
                    if (k != m) {
                        p = H.data[H.address(k, k - 1)];
                        q = H.data[H.address(k + 1, k - 1)];
                        r = (notlast ? H.data[H.address(k + 2, k - 1)] : 0.0);
                        x = Math.abs(p) + Math.abs(q) + Math.abs(r);
                        if (x != 0.0) {
                            p = p / x;
                            q = q / x;
                            r = r / x;
                        }
                    }
                    if (x == 0.0) {
                        break;
                    }
                    s = Math.sqrt(p * p + q * q + r * r);
                    if (p < 0) {
                        s = -s;
                    }
                    if (s != 0) {
                        if (k != m) {
                            H.data[H.address(k, k - 1)] = -s * x;
                        } else if (l != m) {
                            H.data[H.address(k, k - 1)] = -H.data[H.address(k, k - 1)];
                        }
                        p = p + s;
                        x = p / s;
                        y = q / s;
                        z = r / s;
                        q = q / p;
                        r = r / p;

                        // Row modification

                        for (int j = k; j < nn; j++) {
                            p = H.data[H.address(k, j)] + q * H.data[H.address(k + 1, j)];
                            if (notlast) {
                                p = p + r * H.data[H.address(k + 2, j)];
                                H.data[H.address(k + 2, j)] = H.data[H.address(k + 2, j)] - p * z;
                            }
                            H.data[H.address(k, j)] = H.data[H.address(k, j)] - p * x;
                            H.data[H.address(k + 1, j)] = H.data[H.address(k + 1, j)] - p * y;
                        }

                        // Column modification

                        for (int i = 0; i <= Math.min(n, k + 3); i++) {
                            p = x * H.data[H.address(i, k)] + y * H.data[H.address(i, k + 1)];
                            if (notlast) {
                                p = p + z * H.data[H.address(i, k + 2)];
                                H.data[H.address(i, k + 2)] = H.data[H.address(i, k + 2)] - p * r;
                            }
                            H.data[H.address(i, k)] = H.data[H.address(i, k)] - p;
                            H.data[H.address(i, k + 1)] = H.data[H.address(i, k + 1)] - p * q;
                        }

                        // Accumulate transformations

                        for (int i = low; i <= high; i++) {
                            p = x * V.data[V.address(i, k)] + y * V.data[V.address(i, k + 1)];
                            if (notlast) {
                                p = p + z * V.data[V.address(i, k + 2)];
                                V.data[V.address(i, k + 2)] = V.data[V.address(i, k + 2)] - p * r;
                            }
                            V.data[V.address(i, k)] = V.data[V.address(i, k)] - p;
                            V.data[V.address(i, k + 1)] = V.data[V.address(i, k + 1)] - p * q;
                        }
                    } // (s != 0)
                } // k loop
            } // check convergence
        } // while (n >= low)

        // Backsubstitute to find vectors of upper triangular form

        if (norm == 0.0) {
            return;
        }

        for (n = nn - 1; n >= 0; n--) {
            p = d[n];
            q = e[n];

            // Real vector

            if (q == 0) {
                int l = n;
                H.data[H.address(n, n)] = 1.0;
                for (int i = n - 1; i >= 0; i--) {
                    w = H.data[H.address(i, i)] - p;
                    r = 0.0;
                    for (int j = l; j <= n; j++) {
                        r = r + H.data[H.address(i, j)] * H.data[H.address(j, n)];
                    }
                    if (e[i] < 0.0) {
                        z = w;
                        s = r;
                    } else {
                        l = i;
                        if (e[i] == 0.0) {
                            if (w != 0.0) {
                                H.data[H.address(i, n)] = -r / w;
                            } else {
                                H.data[H.address(i, n)] = -r / (eps * norm);
                            }

                            // Solve real equations

                        } else {
                            x = H.data[H.address(i, i + 1)];
                            y = H.data[H.address(i + 1, i)];
                            q = (d[i] - p) * (d[i] - p) + e[i] * e[i];
                            t = (x * s - z * r) / q;
                            H.data[H.address(i, n)] = t;
                            if (Math.abs(x) > Math.abs(z)) {
                                H.data[H.address(i + 1, n)] = (-r - w * t) / x;
                            } else {
                                H.data[H.address(i + 1, n)] = (-s - y * t) / z;
                            }
                        }

                        // Overflow control

                        t = Math.abs(H.data[H.address(i, n)]);
                        if ((eps * t) * t > 1) {
                            for (int j = i; j <= n; j++) {
                                H.data[H.address(j, n)] = H.data[H.address(j, n)] / t;
                            }
                        }
                    }
                }

                // Complex vector

            } else if (q < 0) {
                int l = n - 1;

                // Last vector component imaginary so matrix is triangular

                if (Math.abs(H.data[H.address(n, n - 1)]) > Math.abs(H.data[H.address(n - 1, n)])) {
                    H.data[H.address(n - 1, n - 1)] = q / H.data[H.address(n, n - 1)];
                    H.data[H.address(n - 1, n)] = -(H.data[H.address(n, n)] - p) / H.data[H.address(n, n - 1)];
                } else {
                    cdiv(0.0, -H.data[H.address(n - 1, n)], H.data[H.address(n - 1, n - 1)] - p, q);
                    H.data[H.address(n - 1, n - 1)] = cdivr;
                    H.data[H.address(n - 1, n)] = cdivi;
                }
                H.data[H.address(n, n - 1)] = 0.0;
                H.data[H.address(n, n)] = 1.0;
                for (int i = n - 2; i >= 0; i--) {
                    double ra, sa, vr, vi;
                    ra = 0.0;
                    sa = 0.0;
                    for (int j = l; j <= n; j++) {
                        ra = ra + H.data[H.address(i, j)] * H.data[H.address(j, n - 1)];
                        sa = sa + H.data[H.address(i, j)] * H.data[H.address(j, n)];
                    }
                    w = H.data[H.address(i, i)] - p;

                    if (e[i] < 0.0) {
                        z = w;
                        r = ra;
                        s = sa;
                    } else {
                        l = i;
                        if (e[i] == 0) {
                            cdiv(-ra, -sa, w, q);
                            H.data[H.address(i, n - 1)] = cdivr;
                            H.data[H.address(i, n)] = cdivi;
                        } else {

                            // Solve complex equations

                            x = H.data[H.address(i, i + 1)];
                            y = H.data[H.address(i + 1, i)];
                            vr = (d[i] - p) * (d[i] - p) + e[i] * e[i] - q * q;
                            vi = (d[i] - p) * 2.0 * q;
                            if (vr == 0.0 & vi == 0.0) {
                                vr = eps * norm * (Math.abs(w) + Math.abs(q) + Math.abs(x) + Math.abs(y) + Math.abs(z));
                            }
                            cdiv(x * r - z * ra + q * sa, x * s - z * sa - q * ra, vr, vi);
                            H.data[H.address(i, n - 1)] = cdivr;
                            H.data[H.address(i, n)] = cdivi;
                            if (Math.abs(x) > (Math.abs(z) + Math.abs(q))) {
                                H.data[H.address(i + 1, n - 1)] = (-ra - w * H.data[H.address(i, n - 1)] + q
                                        * H.data[H.address(i, n)])
                                        / x;
                                H.data[H.address(i + 1, n)] = (-sa - w * H.data[H.address(i, n)] - q * H.data[H.address(i, n - 1)])
                                        / x;
                            } else {
                                cdiv(-r - y * H.data[H.address(i, n - 1)], -s - y * H.data[H.address(i, n)], z, q);
                                H.data[H.address(i + 1, n - 1)] = cdivr;
                                H.data[H.address(i + 1, n)] = cdivi;
                            }
                        }

                        // Overflow control

                        t = Math.max(Math.abs(H.data[H.address(i, n - 1)]), Math.abs(H.data[H.address(i, n)]));
                        if ((eps * t) * t > 1) {
                            for (int j = i; j <= n; j++) {
                                H.data[H.address(j, n - 1)] = H.data[H.address(j, n - 1)] / t;
                                H.data[H.address(j, n)] = H.data[H.address(j, n)] / t;
                            }
                        }
                    }
                }
            }
        }

        // Vectors of isolated roots

        for (int i = 0; i < nn; i++) {
            if (i < low | i > high) {
                for (int j = i; j < nn; j++) {
                    V.data[V.address(i, j)] = H.data[H.address(i, j)];
                }
            }
        }

        // Back transformation to get eigenvectors of original matrix

        for (int j = nn - 1; j >= low; j--) {
            for (int i = low; i <= high; i++) {
                z = 0.0;
                for (int k = low; k <= Math.min(j, high); k++) {
                    z = z + V.data[V.address(i, k)] * H.data[H.address(k, j)];
                }
                V.data[V.address(i, j)] = z;
            }
        }
    }

}
