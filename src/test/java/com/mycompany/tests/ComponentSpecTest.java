package com.mycompany.tests;

import com.mycompany.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ComponentSpec class.
 * Tests cover checking component types and the normalization of the kind field.
 * 
 * @author Kamil Fulneczek
 * @version 1.0
 */
public class ComponentSpecTest {

    /**
     * Parameterized test for checking the type of a component based on the kind.
     * 
     * @param kind the kind of the component
     * @param isResistor whether it is expected to be a resistor
     * @param isCapacitor whether it is expected to be a capacitor
     * @param isInductor whether it is expected to be an inductor
     */
    @ParameterizedTest
    @CsvSource({
        "R, true, false, false",
        "C, false, true, false",
        "L, false, false, true",
        "resistor, true, false, false",
        "capacitor, false, true, false",
        "inductor, false, false, true"
    })
    public void testTypeChecks(String kind, boolean isResistor, boolean isCapacitor, boolean isInductor) {
        ComponentSpec component = new ComponentSpec(kind, 1.0);
        assertEquals(isResistor, component.isResistor());
        assertEquals(isCapacitor, component.isCapacitor());
        assertEquals(isInductor, component.isInductor());
    }

    /**
     * Parameterized test for checking normalization of the kind field.
     * 
     * @param inputKind the input kind string
     * @param expectedKind the expected normalized kind
     */
    @ParameterizedTest
    @CsvSource({
        "resistor, R",
        "capacitor, C",
        "inductor, L",
        "R, R",
        "C, C",
        "L, L"
    })
    public void testNormalization(String inputKind, String expectedKind) {
        ComponentSpec component = new ComponentSpec(inputKind, 1.0);
        assertEquals(expectedKind, component.getKind());
    }
}