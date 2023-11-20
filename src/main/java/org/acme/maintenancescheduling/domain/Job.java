package org.acme.maintenancescheduling.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import org.acme.maintenancescheduling.solver.EndDateUpdatingVariableListener;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import ai.timefold.solver.core.api.domain.variable.ShadowVariable;

@PlanningEntity
@Entity
public class Job {

    @Id
    @GeneratedValue
    private Long id;

    private String adres;
    private String bestekcode;
    private int durationInHours;
    private LocalDateTime readyDate; // Inclusive
    private LocalDateTime dueDate; // Exclusive
    private LocalDateTime idealEndDate; // Exclusive

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="JOB_ID")
    private List<JobRequirement> requiredSkills;

    @PlanningVariable(nullable = true)
    @ManyToOne
    private Crew crew;
    
    @PlanningVariable
    private LocalDateTime startDate; // Inclusive
    @ShadowVariable(variableListenerClass = EndDateUpdatingVariableListener.class, sourceVariableName = "startDate")
    private LocalDateTime endDate; // Exclusive

    // No-arg constructor required for Hibernate and Timefold
    public Job() {
    }

    public Job(String adres, String bestekcode, LocalDateTime readyDate, LocalDateTime dueDate, LocalDateTime idealEndDate) {
        this.adres = adres;
        this.bestekcode = bestekcode;
        this.readyDate = readyDate;
        this.dueDate = dueDate;
        this.idealEndDate = idealEndDate;
        updateSkillsfromBestekcode();
        OptionalInt max = requiredSkills.stream()
        .mapToInt(skill -> skill.getDuur()).max();
        this.durationInHours = max.getAsInt();
    }

    public Job(Long id, String adres, String bestekcode, LocalDateTime readyDate, LocalDateTime dueDate, LocalDateTime idealEndDate, Crew crew, LocalDateTime startDate) {
        this.id = id;
        this.adres = adres;
        this.bestekcode = bestekcode;
        this.readyDate = readyDate;
        this.dueDate = dueDate;
        this.idealEndDate = idealEndDate;
        this.crew = crew;
        this.startDate = startDate;
        updateSkillsfromBestekcode();
        OptionalInt max = requiredSkills.stream()
        .mapToInt(skill -> skill.getDuur()).max();
        this.durationInHours = max.getAsInt();
        this.endDate = EndDateUpdatingVariableListener.calculateEndDate(startDate, durationInHours);
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
    public int getdurationInHours() {
        return durationInHours;
    }
    
    public LocalDateTime getReadyDate() {
        return readyDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public LocalDateTime getIdealEndDate() {
        return idealEndDate;
    }

    public List<JobRequirement> getrequiredSkills() {
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

    public void setRequiredSkills(List<JobRequirement> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public void updateSkillsfromBestekcode() {
        // Sort list by typenummer
        List<JobRequirement> requiredSkills = new ArrayList<JobRequirement>(RequirementTranslator.getDefinition(this.bestekcode));
        requiredSkills.sort(Comparator.comparing(JobRequirement::getTypenummer));

        // Group entry's with the same typenummer into a map
        Map<Integer, List<JobRequirement>> reqMap = requiredSkills.stream()
        .collect(Collectors.groupingBy(req -> req.getTypenummer()));

        // Create new list including typenummmer count
        List<JobRequirement> reqsummary = new ArrayList<JobRequirement>();

        for (Map.Entry<Integer, List<JobRequirement>> req : reqMap.entrySet()) {
            reqsummary.add(new JobRequirement(req.getKey(), req.getValue().size(), req.getValue().stream().mapToInt(JobRequirement::getDuur).sum(), req.getValue().iterator().next().getOmschrijving()));
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

}