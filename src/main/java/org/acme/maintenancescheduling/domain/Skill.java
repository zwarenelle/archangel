package org.acme.maintenancescheduling.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Skill{

    @Id
    @GeneratedValue
    private Long id;

    private String typenummer;
    private int aantal;
    private String omschrijving;

    public Skill() { }

    public Skill(String typenummer)
    {
        this.typenummer = typenummer;
    }

    public void setAantal(int aantal)
    {
        this.aantal = aantal;
    }

    public int getAantal()
    {
        return this.aantal;
    }

    public void setTypenummer(String typenummer)
    {
        this.typenummer = typenummer;
    }

    public String getTypenummer()
    {
        return this.typenummer;
    }

    public void setOmschrijving(String omschrijving)
    {
        this.omschrijving = omschrijving;
    }

    public String GetOmschrijving()
    {
        return this.omschrijving;
    }

}