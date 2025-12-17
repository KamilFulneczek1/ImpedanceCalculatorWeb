package com.mycompany.tests;

import com.mycompany.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Inductor class.
 * Tests cover impedance calculations at various frequencies and the description format.
 * 
 * @author Kamil Fulneczek
 * @version 1.0
 */
public class InductorTest {

    /**
     * Parameterized test for verifying the impedance of an Inductor at various frequencies.
     * 
     * @param inductance the inductance value in Henries
     * @param frequency the frequency in Hz
     * @param expectedRe the expected real part of the impedance
     * @param expectedIm the expected imaginary part of the impedance
     * @throws InvalidCircuitException if impedance calculation fails
     */
    @ParameterizedTest
    @CsvSource({
        "0.01, 50.0, 0.0, 3.141592653589793",
        "0.02, 100.0, 0.0, 12.566370614359172",
        "0.005, 200.0, 0.0, 6.283185307179586"
    })
    public void testImpedanceImaginaryForPositiveFrequency(double inductance, double frequency, double expectedRe, double expectedIm) throws InvalidCircuitException {
        Inductor inductor = new Inductor(inductance);
        Complex z = inductor.getImpedance(frequency);
        assertEquals(expectedRe, z.re(), 1e-12);
        assertEquals(expectedIm, z.im(), 1e-9);
    }

    /**
     * Parameterized test for verifying the description format of an Inductor.
     * 
     * @param inductance the inductance value in Henries
     * @param expectedDescription the expected string format of the inductor's description
     */
    @ParameterizedTest
    @CsvSource({
        "0.01, L(0.01)",
        "0.02, L(0.02)",
        "0.005, L(0.005)"
    })
    public void testDescriptionFormat(double inductance, String expectedDescription) {
        Inductor inductor = new Inductor(inductance);
        assertEquals(expectedDescription, inductor.description());
    }
}