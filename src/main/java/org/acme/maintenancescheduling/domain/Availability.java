package org.acme.maintenancescheduling.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;

@Entity
public class Availability {

    @PlanningId
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    Monteur monteur;

    LocalDate date;

    AvailabilityType availabilityType;

    public Availability() {
    }

    public Availability(Monteur monteur, LocalDate date, AvailabilityType availabilityType) {
        this.monteur = monteur;
        this.date = date;
        this.availabilityType = availabilityType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Monteur getJob() {
        return monteur;
    }

    public void setJob(Monteur monteur) {
        this.monteur = monteur;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate localDate) {
        this.date = localDate;
    }

    public AvailabilityType getAvailabilityType() {
        return availabilityType;
    }

    public void setAvailabilityType(AvailabilityType availabilityType) {
        this.availabilityType = availabilityType;
    }

    @Override
    public String toString() {
        return availabilityType + "(" + monteur + ", " + date + ")";
    }
}
