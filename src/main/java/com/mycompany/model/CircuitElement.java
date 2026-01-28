package com.mycompany.model;

/**
 * Base abstraction for elements that can provide impedance and a short description.
 *
 * Implementations include primitive components (Resistor, Capacitor, Inductor)
 * and composite connection nodes (series/parallel groups).
 *
 * @author Kamil Fulneczek
 * @version 1.1
 */
public abstract class CircuitElement {

        /**
     * Protected no-arg constructor.
     *
     *
     * Subclasses should initialize their own state via their constructors.
     */
    protected CircuitElement() {
    }
    /**
     * Compute impedance of this element at the given frequency.
     *
     * @param frequencyHz frequency in Hertz (must be > 0)
     * @return impedance as a Complex
     * @throws InvalidCircuitException when input is invalid for this element or computation cannot be performed
     */
    public abstract Complex getImpedance(double frequencyHz) throws InvalidCircuitException;

    /**
     * Return a short human-readable description of this element suitable for display,
     * for example "R(100)" or "series(R(100), C(1e-6))".
     *
     * @return description string (never null)
     */
    public abstract String description();

    @Override
    public String toString() {
        return description();
    }
}