package com.mycompany.tests;

import com.mycompany.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Complex class.
 * Tests cover arithmetic operations including addition, multiplication,
 * reciprocal, and magnitude, as well as exception handling for undefined
 * operations.
 * 
 * @author Kamil Fulneczek
 * @version 1.0
 */
public class ComplexTest {

    /**
     * Parameterized test for addition of complex numbers.
     * 
     * @param re1 real part of the first complex number
     * @param im1 imaginary part of the first complex number
     * @param re2 real part of the second complex number
     * @param im2 imaginary part of the second complex number
     * @param expectedRe expected real part of the result
     * @param expectedIm expected imaginary part of the result
     */
    @ParameterizedTest
    @CsvSource({
        "1.0, 2.0, 3.0, 4.0, 4.0, 6.0",
        "-1.0, -2.0, 1.0, 2.0, 0.0, 0.0",
        "0.0, 0.0, 0.0, 0.0, 0.0, 0.0"
    })
    public void testAdd(double re1, double im1, double re2, double im2, double expectedRe, double expectedIm) {
        Complex a = new Complex(re1, im1);
        Complex b = new Complex(re2, im2);
        Complex result = a.add(b);
        assertEquals(expectedRe, result.re(), 1e-12);
        assertEquals(expectedIm, result.im(), 1e-12);
    }

    /**
     * Parameterized test for multiplication of complex numbers.
     * 
     * @param re1 real part of the first complex number
     * @param im1 imaginary part of the first complex number
     * @param re2 real part of the second complex number
     * @param im2 imaginary part of the second complex number
     * @param expectedRe expected real part of the result
     * @param expectedIm expected imaginary part of the result
     */
    @ParameterizedTest
    @CsvSource({
        "1.0, 2.0, 3.0, 4.0, -5.0, 10.0",
        "0.0, 1.0, 0.0, 1.0, -1.0, 0.0",
        "1.0, 0.0, 0.0, 0.0, 0.0, 0.0"
    })
    public void testMultiply(double re1, double im1, double re2, double im2, double expectedRe, double expectedIm) {
        Complex a = new Complex(re1, im1);
        Complex b = new Complex(re2, im2);
        Complex result = a.multiply(b);
        assertEquals(expectedRe, result.re(), 1e-12);
        assertEquals(expectedIm, result.im(), 1e-12);
    }

    /**
     * Parameterized test for reciprocal of complex numbers.
     * 
     * @param re real part of the complex number
     * @param im imaginary part of the complex number
     * @param expectedRe expected real part of the reciprocal
     * @param expectedIm expected imaginary part of the reciprocal
     */
    @ParameterizedTest
    @CsvSource({
        "1.0, 1.0, 0.5, -0.5",
        "2.0, -2.0, 0.25, 0.25",
        "0.5, 0.0, 2.0, 0.0"
    })
    public void testReciprocal(double re, double im, double expectedRe, double expectedIm) {
        Complex z = new Complex(re, im);
        Complex result = z.reciprocal();
        assertEquals(expectedRe, result.re(), 1e-12);
        assertEquals(expectedIm, result.im(), 1e-12);
    }

    /**
     * Parameterized test for magnitude of complex numbers.
     * 
     * @param re real part of the complex number
     * @param im imaginary part of the complex number
     * @param expectedMagnitude expected magnitude of the complex number
     */
    @ParameterizedTest
    @CsvSource({
        "3.0, 4.0, 5.0",
        "1.0, 1.0, 1.41421356237",
        "0.0, 0.0, 0.0"
    })
    public void testMagnitude(double re, double im, double expectedMagnitude) {
        Complex z = new Complex(re, im);
        double result = z.magnitude();
        assertEquals(expectedMagnitude, result, 1e-9);
    }

    /**
     * Test for ensuring an exception is thrown when computing the reciprocal of zero.
     */
    @Test
    public void testReciprocalOfZeroThrows() {
        Complex zero = new Complex(0.0, 0.0);
        assertThrows(ArithmeticException.class, zero::reciprocal);
    }
}