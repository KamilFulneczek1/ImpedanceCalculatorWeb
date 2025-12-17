package com.mycompany.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Resistor primitive component.
 *
 * Represents a resistor with a resistance in ohms.
 *
 * @author Kamil Fulneczek
 * @version 1.2
 */
public class Resistor extends Component {

    /**
     * Create a resistor with the given resistance.
     *
     * @param value resistance in ohms
     */
    public Resistor(double value) {
        super(value);
    }

    @Override
    public Complex getImpedance(double frequencyHz) {
        return new Complex(getValue(), 0.0);
    }

    @Override
    public String description() {
        return "R(" + getValue() + ")";
    }
}