package org.acme.maintenancescheduling.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;

@Entity
public class Beschikbaarheid {

    @PlanningId
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    Monteur monteur;

    LocalDate date;

    BeschikbaarheidType beschikbaarheidType;

    public Beschikbaarheid() {
    }

    public Beschikbaarheid(Monteur monteur, LocalDate date, BeschikbaarheidType beschikbaarheidType) {
        this.monteur = monteur;
        this.date = date;
        this.beschikbaarheidType = beschikbaarheidType;
    }

    @Override
    public String toString() {
        return beschikbaarheidType + "(" + monteur + ", " + date + ")";
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

    public Monteur getMonteur() {
        return monteur;
    }

    public void setDate(LocalDate localDate) {
        this.date = localDate;
    }

    public BeschikbaarheidType getBeschikbaarheidType() {
        return beschikbaarheidType;
    }

    public void setBeschikbaarheidType(BeschikbaarheidType beschikbaarheidType) {
        this.beschikbaarheidType = beschikbaarheidType;
    }

    public void setMonteur(Monteur monteur) {
        this.monteur = monteur;
    }
}