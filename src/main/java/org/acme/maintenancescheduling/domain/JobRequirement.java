package org.acme.maintenancescheduling.domain;

import jakarta.persistence.Entity;

@Entity
public class JobRequirement extends Skill{

    public JobRequirement()
    {

    }

    public JobRequirement(int typenummer, int aantal, String omschrijving)
    {
        super(typenummer, aantal, omschrijving);
    }

}