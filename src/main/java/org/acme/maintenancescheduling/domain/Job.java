package org.acme.maintenancescheduling.domain;

import java.time.LocalDateTime;
import java.util.Set;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

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
    private int durationInDays;
    private int duurInUren;
    private LocalDateTime readyDate; // Inclusive
    private LocalDateTime dueDate; // Exclusive
    private LocalDateTime idealEndDate; // Exclusive

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> tagSet;

    @PlanningVariable
    @ManyToOne
    private Crew crew;
    // Follows the TimeGrain Design Pattern
    @PlanningVariable
    private LocalDateTime startDate; // Inclusive
    @ShadowVariable(variableListenerClass = EndDateUpdatingVariableListener.class, sourceVariableName = "startDate")
    private LocalDateTime endDate; // Exclusive

    // No-arg constructor required for Hibernate and Timefold
    public Job() {
    }

    public Job(String adres, int durationInDays, int duurInUren, LocalDateTime readyDate, LocalDateTime dueDate, LocalDateTime idealEndDate, Set<String> tagSet) {
        this.adres = adres;
        this.durationInDays = durationInDays;
        this.duurInUren = duurInUren;
        this.readyDate = readyDate;
        this.dueDate = dueDate;
        this.idealEndDate = idealEndDate;
        this.tagSet = tagSet;
    }

    public Job(Long id, String adres, int durationInDays, int duurInUren, LocalDateTime readyDate, LocalDateTime dueDate, LocalDateTime idealEndDate, Set<String> tagSet,
            Crew crew, LocalDateTime startDate) {
        this.id = id;
        this.adres = adres;
        this.durationInDays = durationInDays;
        this.duurInUren = duurInUren;
        this.readyDate = readyDate;
        this.dueDate = dueDate;
        this.idealEndDate = idealEndDate;
        this.tagSet = tagSet;
        this.crew = crew;
        this.startDate = startDate;
        this.endDate = EndDateUpdatingVariableListener.calculateEndDate(startDate, duurInUren);
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

    public int getDurationInDays() {
        return durationInDays;
    }

    public int getDuurInUren() {
        return duurInUren;
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

    public Set<String> getTagSet() {
        return tagSet;
    }

    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
