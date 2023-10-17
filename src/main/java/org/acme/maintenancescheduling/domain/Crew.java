package org.acme.maintenancescheduling.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;

@Entity
public class Crew{

    @PlanningId
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    private Skill skills;

    // No-arg constructor required for Hibernate
    public Crew() {
    }

    public Crew(String name, Skill skills) {
        this.name = name;
        this.skills = skills;
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

    public Skill getSkills() {
        return skills;
    }

    public void setSkillSet(Skill skills) {
        this.skills = skills;
    }

}
