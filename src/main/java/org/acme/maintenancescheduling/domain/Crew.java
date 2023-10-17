package org.acme.maintenancescheduling.domain;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> skillSet;

    // No-arg constructor required for Hibernate
    public Crew() {
    }

    public Crew(String name, List<String> skillSet) {
        this.name = name;
        this.skillSet = skillSet;
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

    public List<String> getSkillSet() {
        return skillSet;
    }

    public void setSkillSet(List<String> skillSet) {
        this.skillSet = skillSet;
    }

}
