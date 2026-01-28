package com.mycompany.model;

import lombok.Getter;

/**
 * Wrapper that represents a top-level circuit (for display and API).
 *
 * The CompositeCircuit holds a root ConnectionNode representing the circuit structure.
 * This class is primarily a thin wrapper used by views for printing and by tests.
 *
 * @author Kamil Fulneczek
 * @version 1.2
 */
@Getter
public class CompositeCircuit {

    private final ConnectionNode root;

    /**
     * Construct a CompositeCircuit from a ConnectionNode.
     *
     * @param root root connection node; must not be null
     * @throws IllegalArgumentException when root is null
     */
    public CompositeCircuit(ConnectionNode root) {
        if (root == null) throw new IllegalArgumentException("root must not be null");
        this.root = root;
    }

    /**
     * Return the underlying connection node.
     *
     * @return root ConnectionNode
     */
    public ConnectionNode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}