package com.mycompany.tests;

import com.mycompany.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Capacitor class.
 * Tests cover impedance calculations at various frequencies and the description format.
 * 
 * @author Kamil Fulneczek
 * @version 1.0
 */
public class CapacitorTest {

    /**
     * Parameterized test for verifying the impedance of a Capacitor at various frequencies.
     * 
     * @param capacitance the capacitance value in Farads
     * @param frequency the frequency in Hz
     * @param expectedRe the expected real part of the impedance
     * @param expectedIm the expected imaginary part of the impedance
     * @throws InvalidCircuitException if impedance calculation fails
     */
    @ParameterizedTest
    @CsvSource({
        "1e-6, 50.0, 0.0, -3183.098861837907",
        "1e-6, 100.0, 0.0, -1591.5494309189534",
        "2e-6, 100.0, 0.0, -795.7747154594767"
    })
    public void testImpedanceImaginaryForPositiveFrequency(double capacitance, double frequency, double expectedRe, double expectedIm) throws InvalidCircuitException {
        Capacitor capacitor = new Capacitor(capacitance);
        Complex z = capacitor.getImpedance(frequency);
        assertEquals(expectedRe, z.re(), 1e-12);
        assertEquals(expectedIm, z.im(), 1e-9);
    }

    /**
     * Parameterized test for verifying the description format of a Capacitor.
     * 
     * @param capacitance the capacitance value in Farads
     * @param expectedDescription the expected string format of the capacitor's description
     */
    @ParameterizedTest
    @CsvSource({
        "1e-6, C(1.0E-6)",
        "2e-6, C(2.0E-6)",
        "5e-5, C(5.0E-5)"
    })
    public void testDescriptionFormat(double capacitance, String expectedDescription) {
        Capacitor capacitor = new Capacitor(capacitance);
        assertEquals(expectedDescription, capacitor.description());
    }
}