package org.acme.maintenancescheduling.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;

@Entity
public class Crew {

    @PlanningId
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    // 0 = Elektra, 1 = Gas
    private int discipline;

    // No-arg constructor required for Hibernate
    public Crew() {
    }

    public Crew(String name, int discipline) {
        this.name = name;
        this.discipline = discipline;
    }

    public Crew(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDiscipline() {
        return discipline;
    }

    public void setDiscipline(int discipline) {
        this.discipline = discipline;
    }

}
