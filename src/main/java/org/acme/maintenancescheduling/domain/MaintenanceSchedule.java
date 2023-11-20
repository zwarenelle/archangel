package org.acme.maintenancescheduling.domain;

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
public class MaintenanceSchedule {

    @ProblemFactProperty
    private WorkCalendar workCalendar;
    @ProblemFactCollectionProperty
    List<Availability> availabilityList;
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

    // No-arg constructor required for Timefold
    public MaintenanceSchedule() {
    }

    public MaintenanceSchedule(WorkCalendar workCalendar, List<Availability> availabilityList,
            List<Crew> crewList, List<Monteur> monteurList, List<Job> jobList) {
        this.workCalendar = workCalendar;
        this.availabilityList = availabilityList;
        this.crewList = crewList;
        this.monteurList = monteurList;
        this.jobList = jobList;
    }

    @ValueRangeProvider
    public CountableValueRange<LocalDateTime> createStartDateList() {
        return ValueRangeFactory.createLocalDateTimeValueRange(
            workCalendar.getFromDate(), workCalendar.getToDate(),
            1, ChronoUnit.HOURS);
    }

    public List<Availability> getAvailabilityList() {
        return availabilityList;
    }

    public WorkCalendar getWorkCalendar() {
        return workCalendar;
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

    public void setAvailabilityList(List<Availability> availabilityList) {
        this.availabilityList = availabilityList;
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
