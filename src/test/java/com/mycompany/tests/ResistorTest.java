package com.mycompany.tests;

import com.mycompany.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Resistor class.
 * Tests cover both normal impedance calculations and the description format.
 * 
 * @author Kamil Fulneczek
 * @version 1.0
 */
public class ResistorTest {

    /**
     * Parameterized test for verifying the impedance of a Resistor for various values.
     * 
     * @param resistance the resistance value in Ohms
     * @param frequency the frequency in Hz (not used for resistors but required by API)
     * @param expectedRe the expected real part of the impedance
     * @param expectedIm the expected imaginary part of the impedance
     * @throws InvalidCircuitException if impedance calculation fails
     */
    @ParameterizedTest
    @CsvSource({
        "100.0, 1000.0, 100.0, 0.0",
        "47.0, 60.0, 47.0, 0.0",
        "1.0, 50.0, 1.0, 0.0"
    })
    public void testImpedanceIsReal(double resistance, double frequency, double expectedRe, double expectedIm) throws InvalidCircuitException {
        Resistor r = new Resistor(resistance);
        Complex z = r.getImpedance(frequency);
        assertEquals(expectedRe, z.re(), 1e-12);
        assertEquals(expectedIm, z.im(), 1e-12);
    }

    /**
     * Parameterized test for verifying the description format of a Resistor.
     * 
     * @param resistance the resistance value in Ohms
     * @param expectedDescription the expected string format of the resistor's description
     */
    @ParameterizedTest
    @CsvSource({
        "47.0, R(47.0)",
        "100.0, R(100.0)",
        "1.0, R(1.0)"
    })
    public void testDescriptionFormat(double resistance, String expectedDescription) {
        Resistor r = new Resistor(resistance);
        assertEquals(expectedDescription, r.description());
    }
}