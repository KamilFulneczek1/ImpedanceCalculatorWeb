package com.mycompany.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Capacitor primitive component.
 *
 * Represents a capacitor with capacitance in farads.
 *
 * @author Kamil Fulneczek
 * @version 1.1
 */
public class Capacitor extends Component {

    /**
     * Create a capacitor with the given capacitance.
     *
     * @param value capacitance in farads
     */
    public Capacitor(double value) {
        super(value);
    }

    @Override
    public Complex getImpedance(double frequencyHz) throws InvalidCircuitException {
        if (frequencyHz <= 0) throw new InvalidCircuitException("frequency must be > 0");
        double omega = 2 * Math.PI * frequencyHz;
        double imag = -1.0 / (omega * getValue());
        return new Complex(0.0, imag);
    }

    @Override
    public String description() {
        return "C(" + getValue() + ")";
    }
}