package nl.heijmans.teamplanning.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

@Entity
public class Ploeg {

    @PlanningId
    @Id
    @GeneratedValue
    private Long id;
    private String naam;

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="PLOEG_ID")
    private List<Monteur> monteurs;

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="PLOEG_ID")
    private List<PloegSkill> ploegSkill;

    // No-arg constructor required for Hibernate
    public Ploeg() {
    }

    public Ploeg(String naam, List<Monteur> monteurs) {
        this.naam = naam;
        this.monteurs = monteurs;
        this.setPloegSkill();
    }

    @Override
    public String toString() {
        return this.naam;
    }

    public Long getId() {
        return id;
    }

    public String getNaam() {
        return this.naam;
    }

    public List<Monteur> getMonteurs() {
        return monteurs;
    }

    public List<PloegSkill> getPloegSkill() {
        return this.ploegSkill;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }
    
    public void setMonteurs(List<Monteur> monteurs) {
        this.monteurs = monteurs;
    }

    public void addMonteur(Monteur monteur) {
        this.monteurs.add(monteur);
    }

    public void removeMonteur(String naam) {
        this.monteurs = monteurs.stream()
        .filter(monteur -> !(monteur.getNaam().equals(naam)))
        .collect(Collectors.toList());
    }

    public Ploeg filter(List<Monteur> monteursToKeep) {
        Ploeg newploeg = new Ploeg(this.naam, monteursToKeep);
        return newploeg;
    }

    public void setPloegSkill() {
        // Werk PloegSkill bij met de skills van de monteurs, 
        // het doel is om de skills bij elkaar op te tellen indien de typenummers hetzelfde zijn en de lijst te sorteren.
        this.ploegSkill = this.monteurs.stream()
        .map((monteur) -> new PloegSkill(monteur.getVaardigheid().getTypenummer(), 1, monteur.getVaardigheid().getOmschrijving()))
        .collect(Collectors.toList());

        // Sort list by typenummer, just to be sure
        this.ploegSkill.sort(Comparator.comparing(PloegSkill::getTypenummer));

        // Group entry's with the same typenummer into a map
        Map<Integer, List<PloegSkill>> skillMap = this.ploegSkill.stream()
        .collect(Collectors.groupingBy(ploegskill -> ploegskill.getTypenummer()));

        // Create new list including typenummmer count
        List<PloegSkill> skillsummary = new ArrayList<PloegSkill>();

        for (Map.Entry<Integer, List<PloegSkill>> ploegskill : skillMap.entrySet()) {
            skillsummary.add(new PloegSkill(ploegskill.getKey(), ploegskill.getValue().size(), ploegskill.getValue().iterator().next().getOmschrijving()));
        }

        this.ploegSkill = skillsummary;
    }

}