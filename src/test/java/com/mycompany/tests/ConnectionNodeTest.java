package com.mycompany.tests;

import com.mycompany.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ConnectionNode class.
 * Tests cover impedance calculations for series and parallel connections and the description format.
 * 
 * @author Kamil Fulneczek
 * @version 1.0
 */
public class ConnectionNodeTest {

    /**
     * Parameterized test for verifying the impedance of a series connection of resistors.
     * 
     * @param resistance1 the resistance value of the first resistor in Ohms
     * @param resistance2 the resistance value of the second resistor in Ohms
     * @param expectedRe the expected real part of the total impedance
     * @param frequency the frequency in Hz (not relevant for resistors but required by API)
     * @throws InvalidCircuitException if impedance calculation fails
     */
    @ParameterizedTest
    @CsvSource({
        "100.0, 50.0, 150.0, 1000.0",
        "47.0, 33.0, 80.0, 60.0",
        "10.0, 20.0, 30.0, 50.0"
    })
    public void testSeriesSumOfResistors(double resistance1, double resistance2, double expectedRe, double frequency) throws InvalidCircuitException {
        ConnectionNode node = new ConnectionNode(true);
        node.addChild(new Resistor(resistance1));
        node.addChild(new Resistor(resistance2));
        Complex z = node.getImpedance(frequency);
        assertEquals(expectedRe, z.re(), 1e-12);
        assertEquals(0.0, z.im(), 1e-12);
    }

    /**
     * Parameterized test for verifying the impedance of a parallel connection of equal resistors.
     * 
     * @param resistance the resistance value of each resistor in Ohms
     * @param expectedRe the expected real part of the total impedance
     * @param frequency the frequency in Hz (not relevant for resistors but required by API)
     * @throws InvalidCircuitException if impedance calculation fails
     */
    @ParameterizedTest
    @CsvSource({
        "100.0, 50.0, 1000.0",
        "47.0, 23.5, 60.0",
        "10.0, 5.0, 50.0"
    })
    public void testParallelOfEqualResistors(double resistance, double expectedRe, double frequency) throws InvalidCircuitException {
        ConnectionNode node = new ConnectionNode(false);
        node.addChild(new Resistor(resistance));
        node.addChild(new Resistor(resistance));
        Complex z = node.getImpedance(frequency);
        assertEquals(expectedRe, z.re(), 1e-9);
        assertEquals(0.0, z.im(), 1e-12);
    }

    /**
     * Test for verifying the description of a connection node containing multiple components.
     * Checks the format of the description string.
     */
    @Test
    public void testDescriptionShowsChildren() {
        ConnectionNode node = new ConnectionNode(true);
        node.addChild(new Resistor(100.0));
        node.addChild(new Capacitor(1e-6));
        String desc = node.description();
        assertTrue(desc.startsWith("series("));
        assertTrue(desc.contains("R(100.0)"));
        assertTrue(desc.contains("C(1.0E-6)") || desc.contains("C(1.0e-6)"));
    }
}