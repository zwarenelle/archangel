package org.acme.maintenancescheduling.domain;

import jakarta.persistence.Entity;

@Entity
public class CrewSkills extends Skill{

    public CrewSkills()
    {

    }

    public CrewSkills(int typenummer, int aantal, String omschrijving)
    {
        super(typenummer, aantal, omschrijving);
    }

}