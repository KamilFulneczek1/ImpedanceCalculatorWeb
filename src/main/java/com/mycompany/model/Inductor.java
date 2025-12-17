package com.mycompany.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Inductor primitive component.
 *
 * Represents an inductor with inductance in Henrys.
 *
 * @author Kamil Fulneczek
 * @version 1.1
 */
public class Inductor extends Component {

    /**
     * Create an inductor with the given inductance.
     *
     * @param value inductance in Henrys
     */
    public Inductor(double value) {
        super(value);
    }

    @Override
    public Complex getImpedance(double frequencyHz) throws InvalidCircuitException {
        if (frequencyHz <= 0) throw new InvalidCircuitException("frequency must be > 0");
        double omega = 2 * Math.PI * frequencyHz;
        double imag = omega * getValue();
        return new Complex(0.0, imag);
    }

    @Override
    public String description() {
        return "L(" + getValue() + ")";
    }
}