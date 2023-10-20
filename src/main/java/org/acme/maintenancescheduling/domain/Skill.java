package org.acme.maintenancescheduling.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity(name="skills")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Skill{

    @Id
    @GeneratedValue
    private Long id;

    private int typenummer;
    private int aantal;
    private String omschrijving;

    public Skill() { }

    public Skill(int typenummer, int aantal, String omschrijving)
    {
        this.typenummer = typenummer;
        this.aantal = aantal;
        this.omschrijving = omschrijving;
    }

    @Override
    public String toString() {
        return omschrijving;
    }

    public Long getId() {
        return id;
    }

    public int getTypenummer()
    {
        return this.typenummer;
    }

    public int getAantal()
    {
        return this.aantal;
    }

    public String getOmschrijving()
    {
        return this.omschrijving;
    }

    public void setTypenummer(int typenummer)
    {
        this.typenummer = typenummer;
    }

    public void setAantal(int aantal)
    {
        this.aantal = aantal;
    }

    public void setOmschrijving(String omschrijving)
    {
        this.omschrijving = omschrijving;
    }

}