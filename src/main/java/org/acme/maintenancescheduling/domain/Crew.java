package org.acme.maintenancescheduling.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;

@Entity
public class Crew{

    @PlanningId
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String discipline;

    public String test = "Panache";

    // No-arg constructor required for Hibernate
    public Crew() {
    }

    public Crew(String name, String discipline) {
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

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

}
