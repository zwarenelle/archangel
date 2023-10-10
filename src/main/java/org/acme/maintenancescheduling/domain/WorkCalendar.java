package org.acme.maintenancescheduling.domain;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class WorkCalendar {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime fromDate; // Inclusive
    private LocalDateTime toDate; // Exclusive

    // No-arg constructor required for Hibernate
    public WorkCalendar() {
        
    }

    public WorkCalendar(LocalDateTime fromDate, LocalDateTime toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return fromDate + " - " + toDate;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

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
