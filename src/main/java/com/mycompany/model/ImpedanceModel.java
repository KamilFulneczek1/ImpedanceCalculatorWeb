package com.mycompany.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Application-scoped model that provides access to impedance calculations
 * and maintains a history of computed circuit elements.
 *
 * The model is intended to be instantiated once (for example by an application
 * listener) and shared across all servlet requests during the application lifecycle.
 *
 * The model stores history in synchronized lists so concurrent servlet requests
 * can safely add entries.
 *
 * Example usage:
 * <pre>
 *   ImpedanceModel model = new ImpedanceModel();
 *   Complex z = model.calculateImpedance(new Resistor(100), 1000.0);
 *   List&lt;Complex&gt; results = model.getHistoryResults();
 * </pre>
 *
 * @author Kamil Fulneczek
 * @version 1.1
 */
public class ImpedanceModel {

    /**
     * History of circuit elements that were calculated.
     */
    private final List<CircuitElement> historyElements;

    /**
     * Frequencies corresponding to entries in historyElements (in Hz).
     */
    private final List<Double> historyFrequencies;

    /**
     * Computed impedance results corresponding to historyElements.
     */
    private final List<Complex> historyResults;

    /**
     * Create a new ImpedanceModel with empty, thread-safe history lists.
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
     * @param element circuit element (Resistor, Capacitor, Inductor, or ConnectionNode); must not be null
     * @param frequencyHz frequency in Hertz (semantically should be > 0)
     * @return computed impedance as {@link Complex}
     * @throws InvalidCircuitException if computation fails (for example when frequency is invalid for a component)
     * @throws NullPointerException if element is null
     */
    public Complex calculateImpedance(CircuitElement element, double frequencyHz)
            throws InvalidCircuitException {
        if (element == null) {
            throw new NullPointerException("element must not be null");
        }
        Complex impedance = element.getImpedance(frequencyHz);
        historyElements.add(element);
        historyFrequencies.add(frequencyHz);
        historyResults.add(impedance);
        return impedance;
    }

    /**
     * Return an unmodifiable copy of the list of circuit elements from history.
     *
     * @return unmodifiable list of {@link CircuitElement} objects (snapshot)
     */
    public List<CircuitElement> getHistoryElements() {
        return Collections.unmodifiableList(new ArrayList<>(historyElements));
    }

    /**
     * Return an unmodifiable copy of the list of frequencies from history.
     *
     * @return unmodifiable list of frequencies in Hertz (snapshot)
     */
    public List<Double> getHistoryFrequencies() {
        return Collections.unmodifiableList(new ArrayList<>(historyFrequencies));
    }

    /**
     * Return an unmodifiable copy of the list of impedance results from history.
     *
     * @return unmodifiable list of {@link Complex} impedances (snapshot)
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
     * This removes elements, frequencies and results.
     */
    public void clearHistory() {
        historyElements.clear();
        historyFrequencies.clear();
        historyResults.clear();
    }
}