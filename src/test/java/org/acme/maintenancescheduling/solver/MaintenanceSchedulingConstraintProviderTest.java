package org.acme.maintenancescheduling.solver;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.time.LocalDateTime;
import jakarta.inject.Inject;

import org.acme.maintenancescheduling.domain.Crew;
import org.acme.maintenancescheduling.domain.Job;
import org.acme.maintenancescheduling.domain.MaintenanceSchedule;
import org.acme.maintenancescheduling.domain.Monteur;
import org.acme.maintenancescheduling.domain.Skill;
import org.junit.jupiter.api.Test;
import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MaintenanceSchedulingConstraintProviderTest {

    private static final Crew ALPHA_CREW = new Crew("Alpha crew", List.of(new Monteur("Emiel", new Skill(4, "BEI VP")), new Monteur("Mark", new Skill(6, "BEI VOP"))));
    private static final Crew BETA_CREW = new Crew("Beta crew", List.of(new Monteur("Tom", new Skill(1, "VIAG VP")), new Monteur("Bas", new Skill(3, "VIAG VOP"))));
    private static final LocalDateTime DAY_1 = LocalDateTime.of(LocalDate.of(2023, 10, 1), LocalTime.of(12, 0, 0));
    private static final LocalDateTime DAY_1_A = LocalDateTime.of(LocalDate.of(2023, 10, 1), LocalTime.of(13, 0, 0));
    private static final LocalDateTime DAY_2 = LocalDateTime.of(LocalDate.of(2023, 10, 2), LocalTime.of(12, 0, 0));
    private static final LocalDateTime DAY_3 = LocalDateTime.of(LocalDate.of(2023, 10, 3), LocalTime.of(12, 0, 0));

    @Inject
    ConstraintVerifier<MaintenanceScheduleConstraintProvider, MaintenanceSchedule> constraintVerifier;

    @Test
    public void crewConflict() {
        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::crewConflict)
        // Verify that one crew cannot do two jobs in the same exact time window
                .given(ALPHA_CREW,
                        new Job(1L, "Downtown tunnel", "E1637", null, null, null, ALPHA_CREW, DAY_1),
                        new Job(2L, "Uptown bridge", "E1637", null, null, null, ALPHA_CREW, DAY_1))
                .penalizesBy(2L);
        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::crewConflict)
        // Verify that one crew cannot do two jobs in a time windows overlapping an hour
                .given(ALPHA_CREW,
                        new Job(1L, "Downtown tunnel", "E1637", null, null, null, ALPHA_CREW, DAY_1),
                        new Job(2L, "Uptown bridge", "E1637", null, null, null, ALPHA_CREW, DAY_1_A))
                .penalizesBy(1L);
        // Verify that one crew can do two jobs differing in day of month
        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::crewConflict)
                .given(ALPHA_CREW,
                        new Job(1L, "Downtown tunnel", "E1637", null, null, null, ALPHA_CREW, DAY_1),
                        new Job(2L, "Uptown bridge", "E1637", null, null, null, ALPHA_CREW, DAY_2))
                .penalizesBy(0);
        // Verify that two different crews can work in the same exact time window
        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::crewConflict)
                .given(ALPHA_CREW, BETA_CREW,
                        new Job(1L, "Downtown tunnel", "E1637", null, null, null, ALPHA_CREW, DAY_1),
                        new Job(2L, "Uptown bridge", "G1688", null, null, null, BETA_CREW, DAY_1))
                .penalizesBy(0);
    }

    @Test
    public void resourceCheck() {
        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::resourceCheck)
                .given(
                        new Job(1L, "Downtown tunnel", "E1680", null, null, null, ALPHA_CREW, DAY_1),
                        new Job(2L, "Downtown bridge", "E1680", null, null, null, ALPHA_CREW, DAY_3))
                .penalizesBy(0L);
        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::resourceCheck)
                .given(
                        new Job(1L, "Downtown tunnel", "E1680", null, null, null, BETA_CREW, DAY_1),
                        new Job(2L, "Downtown bridge", "E1680", null, null, null, BETA_CREW, DAY_1))
                .penalizesBy(20L);
        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::resourceCheck)
                .given(
                        new Job(1L, "Downtown tunnel", "G1680", null, null, null, ALPHA_CREW, DAY_1),
                        new Job(2L, "Uptown bridge", "G1680", null, null, null, ALPHA_CREW, DAY_1))
                .penalizesBy(20L);
        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::resourceCheck)
                .given(
                        new Job(1L, "Downtown tunnel", "G1680", null, null, null, BETA_CREW, DAY_2),
                        new Job(2L, "Downtown bridge", "E1680", null, null, null, BETA_CREW, DAY_2))
                .penalizesBy(10L);
    }

}