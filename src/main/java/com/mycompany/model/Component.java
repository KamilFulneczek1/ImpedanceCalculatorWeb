package com.mycompany.model;

import lombok.Getter;

/**
 * Abstract base class for primitive components (Resistor, Capacitor, Inductor).
 *
 * Holds the numeric value common to all primitive components and provides a getter
 * for that value. Concrete subclasses implement impedance calculation and description.
 *
 * @author Kamil Fulneczek
 * @version 1.2
 */
@Getter
public abstract class Component extends CircuitElement {

    /**
     * Numeric parameter of the component:
     * - resistor: resistance in Ohms
     * - capacitor: capacitance in Farads
     * - inductor: inductance in Henrys
     *
     * Protected so subclasses can access it.
     */
    protected final double value;

    /**
     * Create a primitive component with the given value.
     *
     * @param value numeric value (units depend on concrete component)
     */
    protected Component(double value) {
        this.value = value;
    }

    /**
     * Returns the numeric value of the component.
     *
     * @return numeric value (units depend on component type)
     */
    public double getValue() {
        return value;
    }

    /**
     * Returns a short description of the component including its type and value.
     *
     * @return description string
     */
    @Override
    public abstract String description();
}