package nl.heijmans.teamplanning.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity(name = "Monteurs")
public class Monteur {
    @Id
    @GeneratedValue
    private Long id;
    private String naam;

    @OneToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name = "skill_id", referencedColumnName = "id")
    private Skill vaardigheid;

    public Monteur()
    {

    }

    public Monteur(String naam, Skill vaardigheid)
    {
        this.naam = naam;
        this.vaardigheid = vaardigheid;
    }

    @Override
    public String toString() {
        return this.naam;
    }

    public Long getId() {
        return id;
    }

    public String getNaam() {
        return naam;
    }

    public Skill getVaardigheid() {
        return vaardigheid;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public void setVaardigheid(Skill vaardigheid) {
        this.vaardigheid = vaardigheid;
    }

}