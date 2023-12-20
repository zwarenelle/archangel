package nl.heijmans.teamplanning.domain;

import jakarta.persistence.Entity;

@Entity
public class PloegSkill extends Skill{

    private int aantal;

    public PloegSkill()
    {

    }

    public PloegSkill(int typenummer, int aantal, String omschrijving)
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