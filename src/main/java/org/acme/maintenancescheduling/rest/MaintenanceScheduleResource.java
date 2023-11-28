package org.acme.maintenancescheduling.rest;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.acme.maintenancescheduling.domain.Job;
import org.acme.maintenancescheduling.domain.MaintenanceSchedule;
import org.acme.maintenancescheduling.persistence.AvailabilityRepository;
import org.acme.maintenancescheduling.persistence.CrewRepository;
import org.acme.maintenancescheduling.persistence.JobRepository;
import org.acme.maintenancescheduling.persistence.MonteurRepository;
import org.acme.maintenancescheduling.persistence.WorkCalendarRepository;

import ai.timefold.solver.core.api.score.analysis.ScoreAnalysis;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import ai.timefold.solver.core.api.solver.ScoreAnalysisFetchPolicy;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.SolverManager;
import ai.timefold.solver.core.api.solver.SolverStatus;

import io.quarkus.panache.common.Sort;
import io.vertx.core.json.JsonObject;

@Path("/schedule")
public class MaintenanceScheduleResource {

    public static final Long SINGLETON_SCHEDULE_ID = 1L;

    @Inject
    AvailabilityRepository availabilityRepository;
    @Inject
    WorkCalendarRepository workCalendarRepository;
    @Inject
    CrewRepository crewRepository;
    @Inject
    MonteurRepository monteurRepository;
    @Inject
    JobRepository jobRepository;

    @Inject
    SolverManager<MaintenanceSchedule, Long> solverManager;
    @Inject
    SolutionManager<MaintenanceSchedule, HardMediumSoftLongScore> solutionManager;

    // For JSON, open http://localhost:8080/schedule
    @GET
    public MaintenanceSchedule getSchedule() {
        // Get the solver status before loading the solution
        // to avoid the race condition that the solver terminates between them
        SolverStatus solverStatus = getSolverStatus();
        MaintenanceSchedule solution = findById(SINGLETON_SCHEDULE_ID);
        solutionManager.update(solution); // Sets the score
        solution.setSolverStatus(solverStatus);
        return solution;
    }

    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    @Path("analyze")
    public ScoreAnalysis<HardMediumSoftLongScore> analyze(MaintenanceSchedule problem, @QueryParam("fetchPolicy") ScoreAnalysisFetchPolicy fetchPolicy) {
        return fetchPolicy == null ? solutionManager.analyze(problem) : solutionManager.analyze(problem, fetchPolicy);
    }

    @PUT
    @Path("job")
    @Transactional
    public Job update(JsonObject item) {
        Long jobId = item.getLong("id");
        
        Job entity = jobRepository.findById(jobId);
        if(entity == null) {
            throw new NotFoundException();
        }

        Long crewId = item.getLong("group");

        LocalDateTime start = (item.getJsonObject("start")) == null ? null : LocalDateTime.ofInstant(item.getInstant("start"), ZoneId.of("Europe/Amsterdam"));
        LocalDateTime end = (item.getJsonObject("end")) == null ? null : LocalDateTime.ofInstant(item.getInstant("end"), ZoneId.of("Europe/Amsterdam"));

        // Map all fields from the job parameter to the existing entity
        entity.setStartDate((start == null ? null : start));
        entity.setEndDate((end == null ? null : end));
        entity.setCrew((crewId == null ? null : crewRepository.findById(crewId)));
        
        return entity;
    }

    public SolverStatus getSolverStatus() {
        return solverManager.getSolverStatus(SINGLETON_SCHEDULE_ID);
    }

    @POST
    @Path("solve")
    public void solve() {
        solverManager.solveAndListen(SINGLETON_SCHEDULE_ID,
                this::findById,
                this::save);
    }

    @POST
    @Path("stopSolving")
    public void stopSolving() {
        solverManager.terminateEarly(SINGLETON_SCHEDULE_ID);
    }

    @Transactional
    protected MaintenanceSchedule findById(Long id) {
        if (!SINGLETON_SCHEDULE_ID.equals(id)) {
            throw new IllegalStateException("There is no schedule with id (" + id + ").");
        }
        return new MaintenanceSchedule(
                workCalendarRepository.listAll().get(0),
                availabilityRepository.listAll(Sort.by("id")),
                crewRepository.listAll(Sort.by("name").and("id")),
                monteurRepository.listAll(Sort.by("id")),
                jobRepository.listAll(Sort.by("dueDate").and("readyDate").and("adres").and("id"))
                );
    }

    @Transactional
    protected void save(MaintenanceSchedule schedule) {
        for (Job job : schedule.getJobList()) {
            // TODO: This is awfully naive: optimistic locking causes issues if called by the SolverManager
            Job attachedJob = jobRepository.findById(job.getId());
            attachedJob.setCrew(job.getCrew());
            attachedJob.setStartDate(job.getStartDate());
            attachedJob.setEndDate(job.getEndDate());
        }
    }
}