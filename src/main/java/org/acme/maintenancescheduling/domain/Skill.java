package org.acme.maintenancescheduling.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Skill{

    @Id
    @GeneratedValue
    private Long id;

    private String bestekcode;

    public Skill() { }

    public Skill(String bestekcode)
    {
        this.bestekcode = bestekcode;
    }

    public void setBestekcode(String bestekcode)
    {
        this.bestekcode = bestekcode;
    }

    public String getBestekcode()
    {
        return this.bestekcode;
    }

}