package com.mycompany.model;

import java.util.ArrayList;
import java. util.Collections;
import java. util.List;

/**
 * Application-scoped model that provides access to impedance calculations
 * and maintains a history of computed circuit elements.
 *
 * This class wraps the existing circuit element classes and stores calculation
 * results in a synchronized list.  It is designed to be instantiated once and
 * shared across all servlet requests during the application lifecycle. 
 *
 * @author Kamil Fulneczek
 * @version 1.0
 */
public class ImpedanceModel {

    private final List<CircuitElement> historyElements;
    private final List<Double> historyFrequencies;
    private final List<Complex> historyResults;

    /**
     * Create a new ImpedanceModel with empty history. 
     */
    public ImpedanceModel() {
        this.historyElements = Collections.synchronizedList(new ArrayList<>());
        this.historyFrequencies = Collections.synchronizedList(new ArrayList<>());
        this.historyResults = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Calculate impedance for a given circuit element at the specified frequency
     * and store the result in history.
     *
     * @param element circuit element (Resistor, Capacitor, Inductor, or ConnectionNode)
     * @param frequencyHz frequency in Hertz
     * @return computed impedance as Complex
     * @throws InvalidCircuitException if computation fails
     */
    public Complex calculateImpedance(CircuitElement element, double frequencyHz) 
            throws InvalidCircuitException {
        Complex impedance = element.getImpedance(frequencyHz);
        historyElements.add(element);
        historyFrequencies. add(frequencyHz);
        historyResults.add(impedance);
        return impedance;
    }

    /**
     * Return the list of circuit elements from history.
     *
     * @return unmodifiable list of CircuitElement objects
     */
    public List<CircuitElement> getHistoryElements() {
        return Collections.unmodifiableList(new ArrayList<>(historyElements));
    }

    /**
     * Return the list of frequencies from history.
     *
     * @return unmodifiable list of frequencies in Hertz
     */
    public List<Double> getHistoryFrequencies() {
        return Collections. unmodifiableList(new ArrayList<>(historyFrequencies));
    }

    /**
     * Return the list of impedance results from history. 
     *
     * @return unmodifiable list of Complex impedances
     */
    public List<Complex> getHistoryResults() {
        return Collections.unmodifiableList(new ArrayList<>(historyResults));
    }

    /**
     * Return the number of entries in the history.
     *
     * @return number of history entries
     */
    public int getHistorySize() {
        return historyElements.size();
    }

    /**
     * Clear all entries from the history.
     */
    public void clearHistory() {
        historyElements.clear();
        historyFrequencies.clear();
        historyResults.clear();
    }
}