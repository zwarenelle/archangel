package nl.heijmans.teamplanning.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import nl.heijmans.teamplanning.solver.EndDateUpdatingVariableListener;
import nl.heijmans.teamplanning.solver.OpdrachtDifficultyComparator;
import nl.heijmans.teamplanning.translators.RequirementTranslator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import ai.timefold.solver.core.api.domain.variable.ShadowVariable;

@PlanningEntity(difficultyComparatorClass = OpdrachtDifficultyComparator.class)
@Entity
public class Opdracht {

    @Id
    @GeneratedValue
    private Long id;

    private String adres;
    private String bestekcode;
    // 1 grain is equal to Teamplanning.TIME_GRAIN_MINUTES minutes
    private int durationInGrains;

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="JOB_ID")
    private List<OpdrachtRequirement> requiredSkills;

    @PlanningVariable(nullable = true)
    @ManyToOne
    private Crew crew;
    
    @PlanningVariable
    private LocalDateTime startDate; // Inclusive
    @ShadowVariable(variableListenerClass = EndDateUpdatingVariableListener.class, sourceVariableName = "startDate")
    private LocalDateTime endDate; // Exclusive

    // No-arg constructor required for Hibernate and Timefold
    public Opdracht() {
    }

    public Opdracht(String adres, String bestekcode) {
        this.adres = adres;
        this.bestekcode = bestekcode;
        ReqsFromBestekcode();
        OptionalInt max = requiredSkills.stream()
        .mapToInt(skill -> skill.getDuur()).max();
        this.durationInGrains = max.getAsInt() * (60 / Teamplanning.TIME_GRAIN_MINUTES);
    }

    public Opdracht(Long id, String adres, String bestekcode, Crew crew, LocalDateTime startDate) {
        this.id = id;
        this.adres = adres;
        this.bestekcode = bestekcode;
        this.crew = crew;
        this.startDate = startDate;
        ReqsFromBestekcode();
        OptionalInt max = requiredSkills.stream()
        .mapToInt(skill -> skill.getDuur()).max();
        this.durationInGrains = max.getAsInt() * (60 / Teamplanning.TIME_GRAIN_MINUTES);
        this.endDate = EndDateUpdatingVariableListener.calculateEndDate(startDate, durationInGrains);
    }

    @Override
    public String toString() {
        return adres + "(" + id + ")";
    }

    @PlanningId
    public Long getId() {
        return id;
    }

    public String getAdres() {
        return adres;
    }

    public String getBestekcode() {
        return bestekcode;
    }

    public int getDurationInHours() {
        return durationInGrains / (60 / Teamplanning.TIME_GRAIN_MINUTES);
    }

    public int getDurationInMinutes() {
        return durationInGrains * Teamplanning.TIME_GRAIN_MINUTES;
    }

    public int getDurationInGrains() {
        return durationInGrains;
    }

    public List<OpdrachtRequirement> getrequiredSkills() {
        return this.requiredSkills;
    }

    public Crew getCrew() {
        return crew;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }

    public void setRequiredSkills(List<OpdrachtRequirement> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public void ReqsFromBestekcode() {
        // Sort list by typenummer
        List<OpdrachtRequirement> requiredSkills = new ArrayList<OpdrachtRequirement>(RequirementTranslator.getDefinition(this.bestekcode));
        requiredSkills.sort(Comparator.comparing(OpdrachtRequirement::getTypenummer));

        // Group entry's with the same typenummer into a map
        Map<Integer, List<OpdrachtRequirement>> reqMap = requiredSkills.stream()
        .collect(Collectors.groupingBy(req -> req.getTypenummer()));

        // Create new list including typenummmer count
        List<OpdrachtRequirement> reqsummary = new ArrayList<OpdrachtRequirement>();

        for (Map.Entry<Integer, List<OpdrachtRequirement>> req : reqMap.entrySet()) {
            reqsummary.add(new OpdrachtRequirement(req.getKey(), req.getValue().size(), req.getValue().stream().mapToInt(OpdrachtRequirement::getDuur).sum(), req.getValue().iterator().next().getOmschrijving()));
        }
    
        this.requiredSkills = reqsummary;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setBestekcode(String bestekcode) {
        this.bestekcode = bestekcode;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public void setDurationInGrains(int durationInGrains) {
        this.durationInGrains = durationInGrains;
    }

}