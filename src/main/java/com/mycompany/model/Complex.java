package com.mycompany.model;
import java.util.Objects;

/**
 * Simple immutable complex number implementation used for impedance arithmetic.
 *
 * Provides common operations used in impedance calculations: addition, multiplication,
 * reciprocal and magnitude computation.
 *
 * @author Kamil Fulneczek
 * @version 1.2
 *
 * @param re the real part of the complex number
 * @param im the imaginary part of the complex number
 */
public record Complex(double re, double im) {

     /**
     * Add another complex number to this one.
     *
     * @param other other complex value
     * @return new Complex representing this + other
     */
    public Complex add(Complex other) {
        return new Complex(this.re + other.re, this.im + other.im);
    }

    /**
     * Multiply by another complex number.
     *
     * @param other other complex value
     * @return new Complex representing this * other
     */
    public Complex multiply(Complex other) {
        double r = this.re * other.re - this.im * other.im;
        double i = this.re * other.im + this.im * other.re;
        return new Complex(r, i);
    }

    /**
     * Return the complex reciprocal (1 / this).
     *
     * @return reciprocal of this complex
     * @throws ArithmeticException if this complex is zero (division by zero)
     */
    public Complex reciprocal() {
        double denom = re * re + im * im;
        if (denom == 0.0) throw new ArithmeticException("Division by zero in complex reciprocal");
        return new Complex(re / denom, -im / denom);
    }

    /**
     * Compute the magnitude (absolute value) of the complex number.
     *
     * @return magnitude |z|
     */
    public double magnitude() {
        return Math.hypot(re, im);
    }


    /**
     * Return a human-readable string representation.
     *
     * @return string like "a + bj" or "a - bj"
     */
    @Override
    public String toString() {
        if (im >= 0) return String.format("%.6g + %.6gj", re, im);
        else return String.format("%.6g - %.6gj", re, -im);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Complex other)) return false;
        return Double.compare(re, other.re) == 0 && Double.compare(im, other.im) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(re, im);
    }
}