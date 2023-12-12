package org.acme.teamplanning.rest;

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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.acme.teamplanning.domain.Job;
import org.acme.teamplanning.domain.MaintenanceSchedule;
import org.acme.teamplanning.persistence.AgendaRepository;
import org.acme.teamplanning.persistence.BeschikbaarheidRepository;
import org.acme.teamplanning.persistence.CrewRepository;
import org.acme.teamplanning.persistence.JobRepository;
import org.acme.teamplanning.persistence.MonteurRepository;
import org.json.JSONObject;

import ai.timefold.solver.core.api.score.analysis.ScoreAnalysis;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import ai.timefold.solver.core.api.solver.ScoreAnalysisFetchPolicy;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.SolverManager;
import ai.timefold.solver.core.api.solver.SolverStatus;

import io.quarkus.panache.common.Sort;

// import org.jboss.logging.Logger;

@Path("/schedule")
public class MaintenanceScheduleResource {

    // private static final Logger LOG = Logger.getLogger(MaintenanceScheduleResource.class);

    public static final Long SINGLETON_SCHEDULE_ID = 1L;

    @Inject
    BeschikbaarheidRepository beschikbaarheidRepository;
    @Inject
    AgendaRepository agendaRepository;
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
    public Job update(String item) {
        
        // Create JSON Object from received JSON String
        JSONObject itemJackson = new JSONObject(item);
        
        // Get jobId in question from JSON Object
        Long jobId = itemJackson.getLong("id");
        
        // Check if corresponding Job exists and get it
        Job entity = jobRepository.findById(jobId);
        if(entity == null) {
            throw new NotFoundException();
        }

        // Get values that need to be modified in Job from JSON Object
        Long crewId = itemJackson.optLongObject("group");
        LocalDateTime start = itemJackson.optString("start") == "" || itemJackson.optString("start") == null ? null
        : LocalDateTime.ofInstant(Instant.parse(itemJackson.optString("start")), ZoneId.of("Europe/Amsterdam"));
        LocalDateTime end = itemJackson.optString("end") == "" || itemJackson.optString("start") == null ? null
        : LocalDateTime.ofInstant(Instant.parse(itemJackson.optString("end")), ZoneId.of("Europe/Amsterdam"));

        // Modify Job accordingly
        entity.setStartDate(start);
        entity.setEndDate(end);
        entity.setCrew((crewId == 0 || crewId == null ? null : crewRepository.findById(crewId)));
        
        return entity;
    }

    @PUT
    @Path("overlapping")
    @Transactional
    public void set(String boolString) {
        Boolean bool = Boolean.valueOf(boolString);
        MaintenanceSchedule.setOverlapping(bool);
        return;
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
                agendaRepository.listAll().get(0),
                beschikbaarheidRepository.listAll(Sort.by("id")),
                crewRepository.listAll(Sort.by("naam").and("id")),
                monteurRepository.listAll(Sort.by("id")),
                jobRepository.listAll(Sort.by("adres").and("id"))
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