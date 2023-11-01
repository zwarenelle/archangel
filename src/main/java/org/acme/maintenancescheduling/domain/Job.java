package org.acme.maintenancescheduling.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import org.acme.maintenancescheduling.solver.EndDateUpdatingVariableListener;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import ai.timefold.solver.core.api.domain.variable.ShadowVariable;

@PlanningEntity
@Entity
public class Job {

    @Id
    @GeneratedValue
    private Long id;

    private String adres;
    private String bestekcode;
    private int durationInHours;
    private LocalDateTime readyDate; // Inclusive
    private LocalDateTime dueDate; // Exclusive
    private LocalDateTime idealEndDate; // Exclusive

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="JOB_ID")
    private List<JobRequirement> requiredSkills;

    @PlanningVariable
    @ManyToOne
    private Crew crew;
    
    @PlanningVariable
    private LocalDateTime startDate; // Inclusive
    @ShadowVariable(variableListenerClass = EndDateUpdatingVariableListener.class, sourceVariableName = "startDate")
    private LocalDateTime endDate; // Exclusive

    // No-arg constructor required for Hibernate and Timefold
    public Job() {
    }

    public Job(String adres, String bestekcode, LocalDateTime readyDate, LocalDateTime dueDate, LocalDateTime idealEndDate, List<JobRequirement> requiredSkills) {
        this.adres = adres;
        this.bestekcode = bestekcode;
        this.requiredSkills = requiredSkills;
        this.durationInHours = requiredSkills.stream()
        .mapToInt(skill -> skill.getDuur()).sum();
        this.readyDate = readyDate;
        this.dueDate = dueDate;
        this.idealEndDate = idealEndDate;
    }

    public Job(Long id, String adres, String bestekcode, LocalDateTime readyDate, LocalDateTime dueDate, LocalDateTime idealEndDate, List<JobRequirement> requiredSkills,
            Crew crew, LocalDateTime startDate) {
        this.id = id;
        this.adres = adres;
        this.bestekcode = bestekcode;
        this.requiredSkills = requiredSkills;
        this.durationInHours = requiredSkills.stream()
        .mapToInt(skill -> skill.getDuur()).sum();
        this.readyDate = readyDate;
        this.dueDate = dueDate;
        this.idealEndDate = idealEndDate;
        this.crew = crew;
        this.startDate = startDate;
        this.endDate = EndDateUpdatingVariableListener.calculateEndDate(startDate, durationInHours);
    }

    @Override
    public String toString() {
        return adres + "(" + id + ")";
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    @PlanningId
    public Long getId() {
        return id;
    }

    public String getAdres() {
        return adres;
    }

    public String getBestekcode() {
        return bestekcode;
    }
    public int getdurationInHours() {
        return durationInHours;
    }
    
    public LocalDateTime getReadyDate() {
        return readyDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public LocalDateTime getIdealEndDate() {
        return idealEndDate;
    }

    public List<JobRequirement> getrequiredSkills() {
        return this.requiredSkills;
    }

    public Crew getCrew() {
        return crew;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }

    public void setrequiredSkills(List<JobRequirement> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setBestekcode(String bestekcode) {
        this.bestekcode = bestekcode;
    }

}