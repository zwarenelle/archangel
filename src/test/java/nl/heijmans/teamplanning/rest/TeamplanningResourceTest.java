package nl.heijmans.teamplanning.rest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.heijmans.teamplanning.domain.Opdracht;
import nl.heijmans.teamplanning.domain.Teamplanning;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ai.timefold.solver.core.api.solver.SolverStatus;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TeamplanningResourceTest {

    @Inject
    TeamplanningResource teamplanningResource;

    @Test
    @Timeout(600_000)
    public void solveDemoDataUntilFeasible() throws InterruptedException {
        teamplanningResource.solve();
        Teamplanning teamplanning = teamplanningResource.getSchedule();
        while (teamplanning.getSolverStatus() != SolverStatus.NOT_SOLVING
                || !teamplanning.getScore().isFeasible()) {
            // Quick polling (not a Test Thread Sleep anti-pattern)
            // Test is still fast on fast machines and doesn't randomly fail on slow machines.
            Thread.sleep(20L);
            teamplanning = teamplanningResource.getSchedule();
        }
        assertFalse(teamplanning.getOpdrachtList().isEmpty());
        for (Opdracht opdracht : teamplanning.getOpdrachtList()) {
            assertNotNull(opdracht.getCrew());
            assertNotNull(opdracht.getStartDate());
        }
        assertTrue(teamplanning.getScore().isFeasible());
    }
}