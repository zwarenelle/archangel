package org.acme.teamplanning.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.ProblemFactProperty;
import ai.timefold.solver.core.api.domain.valuerange.CountableValueRange;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeFactory;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import ai.timefold.solver.core.api.solver.SolverStatus;

@PlanningSolution
public class Teamplanning {

    @ProblemFactProperty
    private Agenda agenda;
    @ProblemFactCollectionProperty
    private List<Beschikbaarheid> beschikbaarheidList;
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private List<Crew> crewList;
    @ProblemFactCollectionProperty
    private List<Monteur> monteurList;
    @PlanningEntityCollectionProperty
    private List<Job> jobList;

    @PlanningScore
    private HardMediumSoftLongScore score;

    // Ignored by Timefold, used by the UI to display solve or stop solving button
    private SolverStatus solverStatus;

    public static final int TIME_GRAIN_MINUTES = 60;
    private static boolean overlapping = false;

    // No-arg constructor required for Timefold
    public Teamplanning() {
    }

    public Teamplanning(Agenda agenda, List<Beschikbaarheid> beschikbaarheidList,
            List<Crew> crewList, List<Monteur> monteurList, List<Job> jobList) {
        this.agenda = agenda;
        this.beschikbaarheidList = beschikbaarheidList;
        this.crewList = crewList;
        this.monteurList = monteurList;
        this.jobList = jobList;
    }

    @ValueRangeProvider
    public CountableValueRange<LocalDateTime> createStartDateList() {
        return ValueRangeFactory.createLocalDateTimeValueRange(
            agenda.getFromDate(), agenda.getToDate(),
                TIME_GRAIN_MINUTES, ChronoUnit.MINUTES);
    }

    public List<Beschikbaarheid> getBeschikbaarheidList() {
        return beschikbaarheidList;
    }

    public Agenda getWorkCalendar() {
        return agenda;
    }

    public List<Crew> getCrewList() {
        return crewList;
    }

    public List<Monteur> getMonteurList() {
        return monteurList;
    }

    public List<Job> getJobList() {
        return jobList;
    }

    public HardMediumSoftLongScore getScore() {
        return score;
    }

    public boolean getOverlapping() {
        return Teamplanning.overlapping;
    }

    public static void setOverlapping(boolean overlapping) {
        Teamplanning.overlapping = overlapping;
    }

    public void setBeschikbaarheidList(List<Beschikbaarheid> beschikbaarheidList) {
        this.beschikbaarheidList = beschikbaarheidList;
    }

    public void setScore(HardMediumSoftLongScore score) {
        this.score = score;
    }

    public SolverStatus getSolverStatus() {
        return solverStatus;
    }

    public void setSolverStatus(SolverStatus solverStatus) {
        this.solverStatus = solverStatus;
    }
}
