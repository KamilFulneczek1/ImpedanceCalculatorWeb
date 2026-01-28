package com.mycompany.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CALCULATION")
public class Calculation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "FREQUENCY")
    private Double frequency;

    @Column(name = "RESULT")
    private String result;

    @OneToMany(mappedBy = "calculation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Component> components = new ArrayList<>();

    public Calculation() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getFrequency() {
        return frequency;
    }

    public void setFrequency(Double frequency) {
        this.frequency = frequency;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public void addComponent(Component c) {
        if (c == null) return;
        c.setCalculation(this);
        this.components.add(c);
    }

    public void removeComponent(Component c) {
        if (c == null) return;
        c.setCalculation(null);
        this.components.remove(c);
    }

    @Override
    public String toString() {
        return "Calculation{id=" + id + ", name='" + name + '\'' + ", frequency=" + frequency + '}';
    }
}