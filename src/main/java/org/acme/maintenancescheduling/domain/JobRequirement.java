package org.acme.maintenancescheduling.domain;

import jakarta.persistence.Entity;

@Entity
public class JobRequirement extends Skill{

    private int aantal;

    public JobRequirement()
    {

    }

    public JobRequirement(int typenummer, int aantal, String omschrijving)
    {
        super(typenummer, omschrijving);
        this.aantal = aantal;
    }
    
    public int getAantal() {
        return aantal;
    }

    public void setAantal(int aantal) {
        this.aantal = aantal;
    }

}