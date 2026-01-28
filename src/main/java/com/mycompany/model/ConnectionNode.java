package com.mycompany.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Composite connection node representing either a series or parallel grouping of
 * CircuitElement children.
 *
 * ConnectionNode composes primitive components (Resistor, Capacitor, Inductor)
 * and other ConnectionNode instances to form a hierarchical circuit structure.
 * The node can operate in two modes:
 * <ul>
 *   <li>series — impedances of children are summed</li>
 *   <li>parallel — reciprocals of impedances are summed and inverted</li>
 * </ul>
 *
 * The children list is backed by a runtime-checked list (Collections.checkedList)
 * to provide additional type-safety at runtime when elements are added.
 *
 * Typical usage:
 * <pre>
 *   ConnectionNode root = new ConnectionNode(true); // series
 *   root.addChild(new Resistor(100));
 *   root.addChild(new ConnectionNode(false)); // nested parallel group
 * </pre>
 *
 * @author Kamil Fulneczek
 * @version 1.2
 */
@Getter
public class ConnectionNode extends CircuitElement {

    private final boolean series;
    private final List<CircuitElement> children = Collections.checkedList(new ArrayList<>(), CircuitElement.class);

    /**
     * Create a connection node.
     *
     * @param series true for series connection, false for parallel
     */
    public ConnectionNode(boolean series) {
        this.series = series;
    }

    /**
     * Add a child element to this connection.
     *
     * @param element element to add; must not be null
     */
    public void addChild(CircuitElement element) {
        if (element == null) throw new IllegalArgumentException("element must not be null");
        children.add(element);
    }

    /**
     * Remove a child element from this connection.
     *
     * @param element element to remove
     * @return true if the element was present and removed, false otherwise
     */
    public boolean removeChild(CircuitElement element) {
        return children.remove(element);
    }

    /**
     * Convenience overload to remove a Component child.
     *
     * @param component component to remove
     * @return true if removed, false otherwise
     */
    public boolean removeChild(Component component) {
        return removeChild((CircuitElement) component);
    }

    /**
     * Convenience overload to remove a ConnectionNode child.
     *
     * @param node child connection node to remove
     * @return true if removed, false otherwise
     */
    public boolean removeChild(ConnectionNode node) {
        return removeChild((CircuitElement) node);
    }

    /**
     * Returns whether this node represents a series connection.
     *
     * @return true if series, false if parallel
     */
    public boolean isSeries() {
        return series;
    }

    /**
     * Return a snapshot list of child elements.
     *
     * @return list of child CircuitElement instances
     */
    public List<CircuitElement> getChildren() {
        return new ArrayList<>(children);
    }

    /**
     * Compute equivalent impedance by aggregating child impedances.
     *
     * @param frequencyHz frequency in Hz
     * @return equivalent impedance as Complex
     * @throws InvalidCircuitException when node has no children or a child computation fails
     */
    @Override
    public Complex getImpedance(double frequencyHz) throws InvalidCircuitException {
        if (children.isEmpty()) throw new InvalidCircuitException("Connection node contains no children");
        try {
            if (series) {
                Complex sum = new Complex(0.0, 0.0);
                for (CircuitElement c : children) sum = sum.add(c.getImpedance(frequencyHz));
                return sum;
            } else {
                Complex accReciprocal = null;
                for (CircuitElement c : children) {
                    Complex z = c.getImpedance(frequencyHz);
                    Complex inv = z.reciprocal();
                    if (accReciprocal == null) accReciprocal = inv;
                    else accReciprocal = accReciprocal.add(inv);
                }
                if (accReciprocal == null) throw new InvalidCircuitException("No children to compute");
                return accReciprocal.reciprocal();
            }
        } catch (ArithmeticException ae) {
            throw new InvalidCircuitException("Computation error: " + ae.getMessage());
        }
    }

    @Override
    public String description() {
        String prefix = series ? "series(" : "parallel(";
        String joined = children.stream()
                .map(CircuitElement::description)
                .collect(Collectors.joining(", "));
        return prefix + joined + ")";
    }

    @Override
    public String toString() {
        return description();
    }
}