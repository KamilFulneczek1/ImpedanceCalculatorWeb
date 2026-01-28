package com.mycompany.entities;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "COMPONENT")
public class Component implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "VALUE")
    private Double value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CALCULATION_ID")
    private Calculation calculation;

    public Component() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public String getType() {
        return type;
    }

    public void setType(String type) { this.type = type; }

    public Double getValue() { return value; }

    public void setValue(Double value) { this.value = value; }

    public Calculation getCalculation() { return calculation; }

    public void setCalculation(Calculation calculation) { this.calculation = calculation; }

    @Override
    public String toString() {
        return "Component{id=" + id + ", type='" + type + '\'' + ", value=" + value + '}';
    }
}