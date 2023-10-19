package org.acme.maintenancescheduling.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.jboss.logging.Logger;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.ProblemFactProperty;
import ai.timefold.solver.core.api.domain.valuerange.CountableValueRange;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeFactory;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import ai.timefold.solver.core.api.solver.SolverStatus;

@PlanningSolution
public class MaintenanceSchedule {

private static final Logger LOG = Logger.getLogger(MaintenanceSchedule.class);

    @ProblemFactProperty
    private WorkCalendar workCalendar;
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private List<Crew> crewList;
    @ProblemFactCollectionProperty
    private List<Skill> skillList;
    @PlanningEntityCollectionProperty
    private List<Job> jobList;

    @PlanningScore
    private HardSoftLongScore score;

    // Ignored by Timefold, used by the UI to display solve or stop solving button
    private SolverStatus solverStatus;

    // No-arg constructor required for Timefold
    public MaintenanceSchedule() {
    }

    public MaintenanceSchedule(WorkCalendar workCalendar,
            List<Crew> crewList, List<Skill> skillList, List<Job> jobList) {
        this.workCalendar = workCalendar;
        this.crewList = crewList;
        this.skillList = skillList;
        this.jobList = jobList;
    }

        @ValueRangeProvider
        public CountableValueRange<LocalDateTime> createStartDateList() {
            return ValueRangeFactory.createLocalDateTimeValueRange(
                workCalendar.getFromDate(), workCalendar.getToDate(),
                1, ChronoUnit.HOURS);
        }


    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public WorkCalendar getWorkCalendar() {
        return workCalendar;
    }

    public List<Crew> getCrewList() {
        return crewList;
    }

    public List<Job> getJobList() {
        // LOG.info(jobList);
        return jobList;
    }

    public List<Skill> getSkillList() {
        // for (Skill sl : skillList) {
        //     LOG.info(sl.getOmschrijving());
        // }
        return skillList;
    }

    public HardSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }

    public SolverStatus getSolverStatus() {
        return solverStatus;
    }

    public void setSolverStatus(SolverStatus solverStatus) {
        this.solverStatus = solverStatus;
    }
}
