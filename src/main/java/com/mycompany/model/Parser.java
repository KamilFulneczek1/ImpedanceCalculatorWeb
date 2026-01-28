package com.mycompany.model;

import java.util.Locale;

/**
 * Parser utilities for component tokens and simple numerics.
 *
 * The main entry point parseComponentToken accepts tokens like:
 *   R:100
 *   C:1e-6
 *   L:0.01
 *
 * It normalizes type codes and converts numeric values to double.
 *
 * @author Kamil Fulneczek
 * @version 1.3
 */
public final class Parser {

    private Parser() {
    }

    /**
     * Parse a single component token into a ComponentSpec.
     *
     * Supported token formats:
     *  - "R:100" or "resistor:100"
     *  - "C:1e-6" or "capacitor:1e-6"
     *  - "L:0.01" or "inductor:0.01"
     *
     * @param token input token string
     * @return ComponentSpec representing parsed token
     * @throws IllegalArgumentException on invalid token format or numeric parse error
     */
    public static ComponentSpec parseComponentToken(String token) {
        if (token == null) throw new IllegalArgumentException("token is null");
        String s = token.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("token is empty");

        int idx = s.indexOf(':');
        if (idx < 0) throw new IllegalArgumentException("Expected format T:value");

        String kind = s.substring(0, idx).trim().toUpperCase(Locale.ROOT);
        String valStr = s.substring(idx + 1).trim();
        double val;
        try {
            val = Double.parseDouble(valStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric value: " + valStr);
        }

        if (kind.equals("RESISTOR")) kind = "R";
        else if (kind.equals("CAPACITOR")) kind = "C";
        else if (kind.equals("INDUCTOR")) kind = "L";
        else if (!(kind.equals("R") || kind.equals("C") || kind.equals("L"))) {
            if (kind.startsWith("R")) kind = "R";
            else if (kind.startsWith("C")) kind = "C";
            else if (kind.startsWith("L")) kind = "L";
            else throw new IllegalArgumentException("Unknown component type: " + s.substring(0, idx));
        }

        return new ComponentSpec(kind, val);
    }
}