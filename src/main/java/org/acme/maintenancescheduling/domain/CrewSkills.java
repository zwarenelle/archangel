package org.acme.maintenancescheduling.domain;

import jakarta.persistence.Entity;

@Entity
public class CrewSkills extends Skill{

    private int aantal;

    public CrewSkills()
    {

    }

    @Override
    public String toString() {
        return "Typenummer: " + getTypenummer() + ", Aantal: " + this.aantal + ", Omschrijving: " +  getOmschrijving();
    }

    public CrewSkills(int typenummer, int aantal, String omschrijving)
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