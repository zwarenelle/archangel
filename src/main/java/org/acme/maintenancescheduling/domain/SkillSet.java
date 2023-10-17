package org.acme.maintenancescheduling.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class SkillSet{

    @Id
    @GeneratedValue
    private Long id;

    private String bestekcode;

    public SkillSet() { }

    public SkillSet(String bestekcode)
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