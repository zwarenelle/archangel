package nl.heijmans.teamplanning.rest;

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
import java.util.List;
import java.util.stream.Collectors;

import nl.heijmans.teamplanning.domain.Opdracht;
import nl.heijmans.teamplanning.domain.Teamplanning;
import nl.heijmans.teamplanning.persistence.AgendaRepository;
import nl.heijmans.teamplanning.persistence.BeschikbaarheidRepository;
import nl.heijmans.teamplanning.persistence.PloegRepository;
import nl.heijmans.teamplanning.persistence.OpdrachtRepository;
import nl.heijmans.teamplanning.persistence.MonteurRepository;
import org.json.JSONObject;

import ai.timefold.solver.core.api.score.analysis.ScoreAnalysis;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import ai.timefold.solver.core.api.solver.RecommendedFit;
import ai.timefold.solver.core.api.solver.ScoreAnalysisFetchPolicy;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.SolverManager;
import ai.timefold.solver.core.api.solver.SolverStatus;

import io.quarkus.panache.common.Sort;

@Path("/schedule")
public class TeamplanningResource {

    public static final Long SINGLETON_SCHEDULE_ID = 1L;

    @Inject
    BeschikbaarheidRepository beschikbaarheidRepository;
    @Inject
    AgendaRepository agendaRepository;
    @Inject
    PloegRepository ploegRepository;
    @Inject
    MonteurRepository monteurRepository;
    @Inject
    OpdrachtRepository opdrachtRepository;

    @Inject
    SolverManager<Teamplanning, Long> solverManager;
    @Inject
    SolutionManager<Teamplanning, HardMediumSoftLongScore> solutionManager;

    // For JSON, open http://localhost:8080/schedule
    @GET
    public Teamplanning getSchedule() {
        // Get the solver status before loading the solution
        // to avoid the race condition that the solver terminates between them
        SolverStatus solverStatus = getSolverStatus();
        Teamplanning solution = findById(SINGLETON_SCHEDULE_ID);
        solutionManager.update(solution); // Sets the score
        solution.setSolverStatus(solverStatus);
        return solution;
    }

    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    @Path("analyze")
    public ScoreAnalysis<HardMediumSoftLongScore> analyze(Teamplanning problem, @QueryParam("fetchPolicy") ScoreAnalysisFetchPolicy fetchPolicy) {
        return fetchPolicy == null ? solutionManager.analyze(problem) : solutionManager.analyze(problem, fetchPolicy);
    }

    @GET
    @Path("opdracht/proposition")
    public List<RecommendedFit<Pair, HardMediumSoftLongScore>> fetch(@QueryParam("id") Long id) {
        
        // Check if corresponding opdracht exists and get it
        Opdracht entity = opdrachtRepository.findById(id);
        if(entity == null) {
            throw new NotFoundException();
        }
        
        Teamplanning solution = findById(SINGLETON_SCHEDULE_ID);
        List<RecommendedFit<Pair, HardMediumSoftLongScore>> recommendations =
        solutionManager.recommendFit(solution, entity,
        opdracht -> new Pair(opdracht.getPloeg(), opdracht.getStartDate(), opdracht.getEndDate()))
        .stream().limit(10L).collect(Collectors.toList());
        
        return recommendations;
    }

    @PUT
    @Path("opdracht")
    @Transactional
    public Opdracht update(String item) {
        
        // Create JSON Object from received JSON String
        JSONObject itemJackson = new JSONObject(item);
        
        // Get opdrachtId in question from JSON Object
        Long opdrachtId = itemJackson.getLong("id");
        
        // Check if corresponding opdracht exists and get it
        Opdracht entity = opdrachtRepository.findById(opdrachtId);
        if(entity == null) {
            throw new NotFoundException();
        }

        // Get values that need to be modified in Opdracht from JSON Object
        Long ploegId = itemJackson.optLongObject("group");
        LocalDateTime start = itemJackson.optString("start") == "" || itemJackson.optString("start") == null ? null
        : LocalDateTime.ofInstant(Instant.parse(itemJackson.optString("start")), ZoneId.of("Europe/Amsterdam"));
        LocalDateTime end = itemJackson.optString("end") == "" || itemJackson.optString("start") == null ? null
        : LocalDateTime.ofInstant(Instant.parse(itemJackson.optString("end")), ZoneId.of("Europe/Amsterdam"));

        // Modify opdracht accordingly
        entity.setStartDate(start);
        entity.setEndDate(end);
        entity.setPloeg((ploegId == 0 || ploegId == null ? null : ploegRepository.findById(ploegId)));
        
        return entity;
    }

    @PUT
    @Path("overlapping")
    @Transactional
    public void set(String boolString) {
        Boolean bool = Boolean.valueOf(boolString);
        Teamplanning.setOverlapping(bool);
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
    protected Teamplanning findById(Long id) {
        if (!SINGLETON_SCHEDULE_ID.equals(id)) {
            throw new IllegalStateException("There is no schedule with id (" + id + ").");
        }
        return new Teamplanning(
                agendaRepository.listAll().get(0),
                beschikbaarheidRepository.listAll(Sort.by("id")),
                ploegRepository.listAll(Sort.by("naam").and("id")),
                monteurRepository.listAll(Sort.by("id")),
                opdrachtRepository.listAll(Sort.by("adres").and("id"))
                );
    }

    @Transactional
    protected void save(Teamplanning schedule) {
        for (Opdracht opdracht : schedule.getOpdrachtList()) {
            // TODO: This is awfully naive: optimistic locking causes issues if called by the SolverManager
            Opdracht attachedOpdracht = opdrachtRepository.findById(opdracht.getId());
            attachedOpdracht.setPloeg(opdracht.getPloeg());
            attachedOpdracht.setStartDate(opdracht.getStartDate());
            attachedOpdracht.setEndDate(opdracht.getEndDate());
        }
    }
}