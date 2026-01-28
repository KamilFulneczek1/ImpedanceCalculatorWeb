package com.mycompany.model;

/**
 * Exception thrown when a circuit or component configuration is invalid.
 *
 * It signals validation errors (for example missing components or invalid numeric values)
 * encountered during parsing or impedance computation.
 *
 * @author Kamil Fulneczek
 * @version 1.0
 */
public class InvalidCircuitException extends Exception {

    /**
     * Create exception with message.
     *
     * @param message error message
     */
    public InvalidCircuitException(String message) {
        super(message);
    }

    /**
     * Create exception with message and cause.
     *
     * @param message error message
     * @param cause   underlying cause
     */
    public InvalidCircuitException(String message, Throwable cause) {
        super(message, cause);
    }
}