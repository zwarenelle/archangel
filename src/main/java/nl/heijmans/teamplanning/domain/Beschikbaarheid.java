package nl.heijmans.teamplanning.domain;

import java.time.LocalDateTime;

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

    LocalDateTime start;
    LocalDateTime end;

    BeschikbaarheidType beschikbaarheidType;

    public Beschikbaarheid() {
    }

    public Beschikbaarheid(Monteur monteur, LocalDateTime start, LocalDateTime end, BeschikbaarheidType beschikbaarheidType) {
        this.monteur = monteur;
        this.start = start;
        this.end = end;
        this.beschikbaarheidType = beschikbaarheidType;
    }

    @Override
    public String toString() {
        return beschikbaarheidType + "(" + monteur + ", " + start + ")";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Monteur getOpdracht() {
        return monteur;
    }

    public void setOpdracht(Monteur monteur) {
        this.monteur = monteur;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public Monteur getMonteur() {
        return monteur;
    }

    public void setStart(LocalDateTime localDateTime) {
        this.start = localDateTime;
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

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}