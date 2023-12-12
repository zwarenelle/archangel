package nl.heijmans.teamplanning.solver;

import static ai.timefold.solver.core.api.score.stream.Joiners.equal;
import static ai.timefold.solver.core.api.score.stream.Joiners.overlapping;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import nl.heijmans.teamplanning.domain.Beschikbaarheid;
import nl.heijmans.teamplanning.domain.BeschikbaarheidType;
import nl.heijmans.teamplanning.domain.Opdracht;

import ai.timefold.solver.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintCollectors;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;

public class TeamplanningConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                // Hard constraints
                crewConflict(constraintFactory),
                resourceCheck(constraintFactory),
                // noWeekends(constraintFactory),

                // Medium constraints
                nullCrew(constraintFactory),

                // Soft constraints
                insideWorkHours(constraintFactory)
        };
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    public Constraint crewConflict(ConstraintFactory constraintFactory) {
        // A crew can do at most one opdracht at the same time.
        return constraintFactory
                .forEachUniquePair(Opdracht.class,
                        equal(Opdracht::getCrew),
                        overlapping(Opdracht::getStartDate, Opdracht::getEndDate))
                .filter((opdracht1, opdracht2) -> ChronoUnit.MINUTES.between(
                                opdracht1.getStartDate().isAfter(opdracht2.getStartDate())
                                        ? opdracht1.getStartDate() : opdracht2.getStartDate(),
                                opdracht1.getEndDate().isBefore(opdracht2.getEndDate())
                                        ? opdracht1.getEndDate() : opdracht2.getEndDate()) > 15) // Opdracht's may overlap 15 minutes
                .penalizeLong(HardMediumSoftLongScore.ONE_HARD,
                        (opdracht1, opdracht2) -> ChronoUnit.MINUTES.between(
                                opdracht1.getStartDate().isAfter(opdracht2.getStartDate())
                                        ? opdracht1.getStartDate() : opdracht2.getStartDate(),
                                opdracht1.getEndDate().isBefore(opdracht2.getEndDate())
                                        ? opdracht1.getEndDate() : opdracht2.getEndDate())
                        )
                .asConstraint("Dubbele boeking (ploeg)");
    }

    public Constraint resourceCheck(ConstraintFactory constraintFactory) {
        // Match crewSkill and Availability to OpdrachtRequirements
        return constraintFactory
                .forEach(Opdracht.class)
                // Join beschikbaarheid's on the same date as the startdate of opdracht
                .join(Beschikbaarheid.class, 
                        Joiners.overlapping(Opdracht::getStartDate, Opdracht::getEndDate, Beschikbaarheid::getStart, Beschikbaarheid::getEnd),
                        //Filter monteurs that are actually in the crew that's assigned this opdracht
                        Joiners.filtering((opdracht, beschikbaarheid) -> opdracht.getCrew().getMonteurs().contains(beschikbaarheid.getMonteur())))
                // Summarize the availabilties into a list per opdracht
                .groupBy((opdracht, beschikbaarheid) -> opdracht, ConstraintCollectors.toList((opdracht, beschikbaarheid) -> beschikbaarheid))
                // Filter out if the opdracht needs more monteurs than there are in the crew, cause then all skills will never match
                        .filter((opdracht, beschikbaarheid) ->
                                // Filter crew with monteurs that are available for that day and compare the list size with the opdracht requirements list
                                opdracht.getrequiredSkills().size() > opdracht.getCrew().filter(beschikbaarheid.stream()
                                        .filter(a -> a.getBeschikbaarheidType() == BeschikbaarheidType.BESCHIKBAAR).map(Beschikbaarheid::getMonteur).collect(Collectors.toList()))
                                        .getCrewSkill().size() ||
                                        // If above is not false, it could still be that the skills do not match between (again, filtered) crew and opdracht
                                        !(opdracht.getrequiredSkills().stream()
                                        // For every requirement, search for a suitable monteur in crew and make sure there are enough!
                                        .allMatch(opdrachtreq -> opdracht.getCrew()
                                                .filter(beschikbaarheid.stream()
                                                        .filter(a -> a.getBeschikbaarheidType() == BeschikbaarheidType.BESCHIKBAAR)
                                                        .map(Beschikbaarheid::getMonteur).collect(Collectors.toList()))
                                                .getCrewSkill().stream()
                                .anyMatch(crewskill -> 
                                        (crewskill.getTypenummer() <= 3 ? 
                                                opdrachtreq.getTypenummer() <= crewskill.getTypenummer() : 
                                                opdrachtreq.getTypenummer() > 3 && opdrachtreq.getTypenummer() <= crewskill.getTypenummer())
                                        && opdrachtreq.getAantal() <= crewskill.getAantal())))
                        )
                .penalizeLong(HardMediumSoftLongScore.ONE_HARD,
                        (opdracht, beschikbaarheid) -> opdracht.getrequiredSkills().isEmpty() ? 1L : Long.valueOf(opdracht.getrequiredSkills().size())
                        )
                .asConstraint("Beschikbaarheid");
    }

//     public Constraint noWeekends(ConstraintFactory constraintFactory) {
//         // A crew does not work on weekends
//         return constraintFactory
//                 .forEach(Opdracht.class)
//                         .filter(opdracht -> 
//                                    (opdracht.getStartDate().getDayOfWeek().getValue() > 5)
//                                 || (opdracht.getEndDate().getDayOfWeek().getValue() > 5)
//                                 )
//                 .penalizeLong(HardMediumSoftLongScore.ONE_HARD,
//                         opdracht -> Long.valueOf(Math.max((opdracht.getStartDate().getDayOfWeek().getValue() - 5), (opdracht.getEndDate().getDayOfWeek().getValue() - 5))))
//                 .asConstraint("Overlaps weekend");
//     }

    // ************************************************************************
    // Medium constraints
    // ************************************************************************

    public Constraint nullCrew(ConstraintFactory constraintFactory) {
               return constraintFactory.forEachIncludingNullVars(Opdracht.class)
               .filter(opdracht -> opdracht.getCrew() == null)
               .penalizeLong(HardMediumSoftLongScore.ONE_MEDIUM,
                        opdracht -> 1L)
                .asConstraint("Onplanbaar (geen passende ploeg)");
    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    public Constraint insideWorkHours(ConstraintFactory constraintFactory) {
        // Preferably only work between 7AM en 4PM
        return constraintFactory
                .forEach(Opdracht.class)
                        .filter(opdracht -> opdracht.getStartDate().toLocalTime() != null
                                && // Don't start the opdracht before 7AM or after 4PM
                                        ((opdracht.getStartDate().toLocalTime().isBefore(LocalTime.of(7, 0, 0))
                                        || opdracht.getStartDate().toLocalTime().isAfter(LocalTime.of(16, 0, 0)))
                                || // Don't end it after 4PM or before 7AM
                                        (opdracht.getEndDate().toLocalTime().isAfter(LocalTime.of(16, 0, 0))
                                        || opdracht.getEndDate().toLocalTime().isBefore(LocalTime.of(7, 0, 0))))
                                )
                .penalizeLong(HardMediumSoftLongScore.ONE_SOFT,
                        opdracht -> opdracht.getStartDate().toLocalTime().compareTo(LocalTime.of(7, 0, 0)) < 0
                                ? ChronoUnit.MINUTES.between(opdracht.getStartDate().toLocalTime(), LocalTime.of(7, 0, 0))
                                : opdracht.getEndDate().toLocalTime().compareTo(LocalTime.of(16, 0, 0)) > 0
                                        ? ChronoUnit.MINUTES.between(LocalTime.of(16, 0, 0), opdracht.getEndDate().toLocalTime())
                                        : ChronoUnit.MINUTES.between(opdracht.getEndDate().toLocalTime(), LocalTime.of(7, 0, 0))
                        )
                .asConstraint("Werktijden");
    }

}