package org.heijmans.teamplanning.domain;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Agenda {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime fromDate; // Inclusive
    private LocalDateTime toDate; // Exclusive

    // No-arg constructor required for Hibernate
    public Agenda() {
        
    }

    public Agenda(LocalDateTime fromDate, LocalDateTime toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return fromDate + " - " + toDate;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public LocalDateTime getToDate() {
        return toDate;
    }

}
