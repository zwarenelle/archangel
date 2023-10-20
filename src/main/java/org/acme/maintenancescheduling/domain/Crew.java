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
    private int discipline;

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="CREW_ID")
    private Set<Skill> possessedSkills;

    // No-arg constructor required for Hibernate
    public Crew() {
    }

    // public Crew(String name, int discipline) {
    //     this.name = name;
    //     this.discipline = discipline;
    // }

    public Crew(String name, int discipline, Set<Skill> possessedSkills) {
        this.name = name;
        this.discipline = discipline;
        // this.possessedSkills = possessedSkills;
    }

    // public Crew(Long id, String name) {
    //     this.id = id;
    //     this.name = name;
    // }

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

    public Set<Skill> getPossessedSkills() {
        return this.possessedSkills;
    }

    public void setPossessedSkills(Set<Skill> possessedSkills) {
        this.possessedSkills = possessedSkills;
    }

    public void setDiscipline(int discipline) {
        this.discipline = discipline;
    }

}
