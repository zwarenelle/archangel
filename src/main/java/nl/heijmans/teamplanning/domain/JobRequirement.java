package nl.heijmans.teamplanning.domain;

import jakarta.persistence.Entity;

@Entity
public class JobRequirement extends Skill{

    private int aantal;
    private int duur;

    public JobRequirement()
    {

    }

    public JobRequirement(int typenummer, int aantal, int duur, String omschrijving)
    {
        super(typenummer, omschrijving);
        this.aantal = aantal;
        this.duur = duur;
    }

    @Override
    public String toString() {
        return "Type: " + getTypenummer() + ", Aantal: " + this.aantal + ", Duur: " + this.duur + ", Omschrijving: " + getOmschrijving();
    }
    
    public int getAantal() {
        return aantal;
    }

    public int getDuur() {
        return duur;
    }

    public void setAantal(int aantal) {
        this.aantal = aantal;
    }

    public void setDuur(int duur) {
        this.duur = duur;
    }

}