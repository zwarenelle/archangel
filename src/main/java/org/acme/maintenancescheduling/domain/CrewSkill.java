package org.acme.maintenancescheduling.domain;

import jakarta.persistence.Entity;

@Entity
public class CrewSkill extends Skill{

    private int aantal;

    public CrewSkill()
    {

    }

    public CrewSkill(int typenummer, int aantal, String omschrijving)
    {
        super(typenummer, omschrijving);
        this.aantal = aantal;
    }

    @Override
    public String toString() {
        return "Typenummer: " + getTypenummer() + ", Aantal: " + this.aantal + ", Omschrijving: " +  getOmschrijving();
    }

    public int getAantal() {
        return aantal;
    }

    public void setAantal(int aantal) {
        this.aantal = aantal;
    }

}