package com.mycompany.tests;

import com.mycompany.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Parser class, specifically the parseComponentToken method.
 * Covers parsing of valid and invalid tokens to verify expected outputs
 * and exception handling.
 * 
 * @author Kamil Fulneczek
 * @version 1.0
 */
public class ParserTest {

    /**
     * Parameterized test for parsing valid component tokens.
     * 
     * @param token the input token string
     * @param expectedKind the expected kind of the component
     * @param expectedValue the expected numeric value
     */
    @ParameterizedTest
    @CsvSource({
        "R:100, R, 100.0",
        "resistor:50, R, 50.0",
        "C:1e-6, C, 1e-6",
        "capacitor:1.0e-6, C, 1.0e-6",
        "L:0.01, L, 0.01",
        "inductor:0.02, L, 0.02"
    })
    public void testParseValidTokens(String token, String expectedKind, double expectedValue) {
        ComponentSpec spec = Parser.parseComponentToken(token);
        assertEquals(expectedKind, spec.getKind());
        assertEquals(expectedValue, spec.getValue(), 1e-12);
    }

    /**
     * Test for parsing an invalid token that should throw an exception.
     */
    @Test
    public void testParseInvalidToken() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parseComponentToken("InvalidToken"));
    }

    /**
     * Test for parsing a token with missing value.
     */
    @Test
    public void testParseMissingValue() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parseComponentToken("R:"));
    }

    /**
     * Test for parsing a token with missing kind.
     */
    @Test
    public void testParseMissingKind() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parseComponentToken(":100"));
    }
}