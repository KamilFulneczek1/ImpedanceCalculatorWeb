package com.mycompany.model;

import java.util.Locale;

/**
 * Specification DTO for a single component parsed from user input.
 *
 * It stores the kind of component (resistor/capacitor/inductor) and its numeric value.
 * This class provides helper accessors used by the controller to construct Component instances.
 *
 * @author Kamil Fulneczek
 * @version 1.3
 *
 * @param kind normalized kind code (R, C, L or full name)
 * @param value numeric value of the component (units depend on type)
 */
public record ComponentSpec(String kind, double value) {

    public ComponentSpec {
        if (kind == null) kind = "";
        kind = kind.trim().toUpperCase(Locale.ROOT);
        if (kind.equals("RESISTOR")) kind = "R";
        else if (kind.equals("CAPACITOR")) kind = "C";
        else if (kind.equals("INDUCTOR")) kind = "L";
        else if (!(kind.equals("R") || kind.equals("C") || kind.equals("L"))) {
            if (kind.startsWith("R")) kind = "R";
            else if (kind.startsWith("C")) kind = "C";
            else if (kind.startsWith("L")) kind = "L";
            else kind = "";
        }
    }

    public String getKind() {
        return kind;
    }

    public double getValue() {
        return value;
    }

    public boolean isResistor() {
        return kind.equals("R") || kind.equals("RESISTOR");
    }

    public boolean isCapacitor() {
        return kind.equals("C") || kind.equals("CAPACITOR");
    }

    public boolean isInductor() {
        return kind.equals("L") || kind.equals("INDUCTOR");
    }

    @Override
    public String toString() {
        return kind + ":" + value;
    }
}