package org.acme.maintenancescheduling.domain;

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
public class Crew {

    @PlanningId
    @Id
    @GeneratedValue
    private Long id;
    private String naam;

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="CREW_ID")
    private List<Monteur> monteurs;

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="CREW_ID")
    private List<CrewSkills> crewSkills;

    // No-arg constructor required for Hibernate
    public Crew() {
    }

    public Crew(String naam, List<Monteur> monteurs) {
        this.naam = naam;
        this.monteurs = monteurs;
        this.setCrewSkills();
    }

    @Override
    public String toString() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Monteur> getMonteurs() {
        return monteurs;
    }

    public List<CrewSkills> getCrewSkills() {
        return this.crewSkills;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String naam) {
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

    public Crew filter(List<Monteur> monteursToRemove) {
        Crew newcrew = new Crew(this.naam, monteursToRemove);
        return newcrew;
    }

    public void setCrewSkills() {
        // Update crewSkills with skills from monteurs
        this.crewSkills = this.monteurs.stream()
        .map((monteur) -> new CrewSkills(monteur.getVaardigheid().getTypenummer(), 1, monteur.getVaardigheid().getOmschrijving()))
        .collect(Collectors.toList());

        // Sort list by typenummer, just to be sure
        this.crewSkills.sort(Comparator.comparing(CrewSkills::getTypenummer));

        // Group entry's with the same typenummer into a map
        Map<Integer, List<CrewSkills>> skillMap = this.crewSkills.stream()
        .collect(Collectors.groupingBy(crewskill -> crewskill.getTypenummer()));

        // Create new list including typenummmer count
        List<CrewSkills> skillsummary = new ArrayList<CrewSkills>();

        for (Map.Entry<Integer, List<CrewSkills>> crewskill : skillMap.entrySet()) {
            skillsummary.add(new CrewSkills(crewskill.getKey(), crewskill.getValue().size(), crewskill.getValue().iterator().next().getOmschrijving()));
        }

        this.crewSkills = skillsummary;
    }

}