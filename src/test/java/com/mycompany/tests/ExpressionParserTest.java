package com.mycompany.tests;

import com.mycompany.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ExpressionParser class.
 * Tests cover parsing of single components and nested expressions.
 * 
 * @author Kamil Fulneczek
 * @version 1.0
 */
public class ExpressionParserTest {

    /**
     * Test for parsing a single component expression.
     * Ensures correct parsing of the kind and value.
     */
    @Test
    public void testParseSingleComponent() {
        CircuitElement element = ExpressionParser.parse("R:100");
        assertTrue(element instanceof Resistor);
        assertEquals("R(100.0)", element.description());
    }

    /**
     * Test for parsing a nested expression with series and parallel connections.
     * Verifies the hierarchical structure and correctness of the description.
     */
    @Test
    public void testParseNestedExpression() {
        CircuitElement root = ExpressionParser.parse("series(R:100, parallel(C:1e-6, L:0.01), R:50)");
        assertTrue(root instanceof ConnectionNode);
        String description = root.description();
        assertTrue(description.startsWith("series("));
        assertTrue(description.contains("R(100.0)"));
        assertTrue(description.contains("parallel("));
        assertTrue(description.contains("C(1.0E-6)") || description.contains("C(1.0e-6)"));
        assertTrue(description.contains("L(0.01)"));
    }

    /**
     * Test for parsing an invalid expression.
     * Ensures an IllegalArgumentException is thrown for malformed input.
     */
    @Test
    public void testParseInvalidExpression() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("invalid expression"));
    }
}