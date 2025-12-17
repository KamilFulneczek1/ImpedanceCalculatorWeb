package com.mycompany.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple parser for nested circuit expressions like:
 *   series(R:100, parallel(C:1e-6, L:0.01), R:50)
 *
 * The parser returns a CircuitElement tree (ConnectionNode and Component leaves).
 *
 * Note: This parser is intentionally small and supports the expression form used by the
 * CLI: 'series(...)' and 'parallel(...)' with comma-separated subexpressions and component tokens.
 *
 * @author Kamil Fulneczek
 * @version 1.1
 */
public final class ExpressionParser {

    private ExpressionParser() {
    }

    /**
     * Parse a nested expression and return the root CircuitElement.
     *
     * @param expr expression string (may contain whitespace)
     * @return parsed CircuitElement (ConnectionNode or Component)
     * @throws IllegalArgumentException on parse errors
     */
    public static CircuitElement parse(String expr) {
        if (expr == null) throw new IllegalArgumentException("expression is null");
        String s = expr.trim();
        if (!s.contains("(")) {
            ComponentSpec spec = Parser.parseComponentToken(s);
            if (spec.isResistor()) return new Resistor(spec.getValue());
            if (spec.isCapacitor()) return new Capacitor(spec.getValue());
            if (spec.isInductor()) return new Inductor(spec.getValue());
            throw new IllegalArgumentException("Unknown component spec: " + s);
        }

        int idx = s.indexOf('(');
        String name = s.substring(0, idx).trim().toLowerCase();
        int close = findMatchingParen(s, idx);
        if (close != s.length() - 1) {
            String trailing = s.substring(close + 1).trim();
            if (!trailing.isEmpty()) throw new IllegalArgumentException("Unexpected trailing characters");
        }
        String inner = s.substring(idx + 1, close);

        boolean isSeries = name.startsWith("series");
        boolean isParallel = name.startsWith("parallel");
        if (!isSeries && !isParallel) throw new IllegalArgumentException("Unknown connection type: " + name);

        List<String> parts = splitTopLevel(inner);
        ConnectionNode node = new ConnectionNode(isSeries);
        for (String part : parts) {
            CircuitElement child = parse(part);
            node.addChild(child);
        }
        return node;
    }

    private static int findMatchingParen(String s, int openPos) {
        int depth = 0;
        for (int i = openPos; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') {
                depth--;
                if (depth == 0) return i;
            }
        }
        throw new IllegalArgumentException("Unmatched parenthesis in expression");
    }

    private static List<String> splitTopLevel(String s) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        int depth = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                depth++;
                cur.append(c);
            } else if (c == ')') {
                depth--;
                cur.append(c);
            } else if (c == ',' && depth == 0) {
                out.add(cur.toString().trim());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        if (cur.length() > 0) out.add(cur.toString().trim());
        return out;
    }
}