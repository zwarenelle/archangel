package org.acme.maintenancescheduling.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.util.Set;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;

@Entity
public class Crew{

    @PlanningId
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="CREW_ID")
    private Set<CrewSkills> crewSkills;

    // No-arg constructor required for Hibernate
    public Crew() {
    }

    public Crew(String name, Set<CrewSkills> crewSkills) {
        this.name = name;
        this.crewSkills = crewSkills;
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

    public Set<CrewSkills> getCrewSkills() {
        return this.crewSkills;
    }

    public void setCrewSkills(Set<CrewSkills> crewSkills) {
        this.crewSkills = crewSkills;
    }

}