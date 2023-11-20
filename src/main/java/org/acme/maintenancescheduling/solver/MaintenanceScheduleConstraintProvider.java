package org.acme.maintenancescheduling.solver;

import static ai.timefold.solver.core.api.score.stream.Joiners.equal;
import static ai.timefold.solver.core.api.score.stream.Joiners.overlapping;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.acme.maintenancescheduling.domain.Availability;
import org.acme.maintenancescheduling.domain.AvailabilityType;
import org.acme.maintenancescheduling.domain.Job;

import ai.timefold.solver.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintCollectors;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;

public class MaintenanceScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                // Hard constraints
                crewConflict(constraintFactory),
                skillConflict(constraintFactory),
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
        // A crew can do at most one maintenance job at the same time.
        return constraintFactory
                .forEachUniquePair(Job.class,
                        equal(Job::getCrew),
                        overlapping(Job::getStartDate, Job::getEndDate))
                .penalizeLong(HardMediumSoftLongScore.ONE_HARD,
                        (job1, job2) -> ChronoUnit.HOURS.between(
                                job1.getStartDate().isAfter(job2.getStartDate())
                                        ? job1.getStartDate() : job2.getStartDate(),
                                job1.getEndDate().isBefore(job2.getEndDate())
                                        ? job1.getEndDate() : job2.getEndDate())
                        )
                .asConstraint("Crew conflict");
    }

    public Constraint skillConflict(ConstraintFactory constraintFactory) {
        // Match crewSkills and Availability to JobRequirements
        return constraintFactory
                .forEach(Job.class)
                // Join availability's on the same date as the startdate of job
                .join(Availability.class, 
                        Joiners.equal((Job job) -> job.getStartDate().toLocalDate(), Availability::getDate),
                        //Filter monteurs that are actually in the crew that's assigned this job
                        Joiners.filtering((job, availability) -> job.getCrew().getMonteurs().contains(availability.getMonteur())))
                // Summarize the availabilties into a list per job
                .groupBy((job, availability) -> job, ConstraintCollectors.toList((job, availability) -> availability))
                // Filter out if the job needs more monteurs than there are in the crew, cause then all skills will never match
                        .filter((job, availability) ->
                                // Filter crew with monteurs that are available for that day and compare the list size with the job requirements list
                                job.getrequiredSkills().size() > job.getCrew().filter(availability.stream().filter(a -> a.getAvailabilityType() == AvailabilityType.AVAILABLE).map(Availability::getMonteur).collect(Collectors.toList()))
                                        .getCrewSkills().size() ||
                                // If above is not false, it could still be that the skills do not match between (again, filtered) crew and job
                                !(job.getrequiredSkills().stream()
                                // For every requirement, search for a suitable monteur in crew and make sure there are enough!
                                .allMatch(jobreq -> job.getCrew()
                                        .filter(availability.stream()
                                                .filter(a -> a.getAvailabilityType() == AvailabilityType.AVAILABLE)
                                                .map(Availability::getMonteur).collect(Collectors.toList()))
                                        .getCrewSkills().stream()
                                .anyMatch(crewskill -> 
                                        (crewskill.getTypenummer() <= 3 ? 
                                                jobreq.getTypenummer() <= crewskill.getTypenummer() : 
                                                jobreq.getTypenummer() > 3 && jobreq.getTypenummer() <= crewskill.getTypenummer())
                                        && jobreq.getAantal() <= crewskill.getAantal())))
                        )
                .penalizeLong(HardMediumSoftLongScore.ONE_HARD,
                        (job, availability) -> job.getrequiredSkills().isEmpty() ? 1L : Long.valueOf(job.getrequiredSkills().size())
                        )
                .asConstraint("Skill / Availability Conflict");
    }

//     public Constraint noWeekends(ConstraintFactory constraintFactory) {
//         // A crew does not work on weekends
//         return constraintFactory
//                 .forEach(Job.class)
//                         .filter(job -> 
//                                    (job.getStartDate().getDayOfWeek().getValue() > 5)
//                                 || (job.getEndDate().getDayOfWeek().getValue() > 5)
//                                 )
//                 .penalizeLong(HardMediumSoftLongScore.ONE_HARD,
//                         job -> Long.valueOf(Math.max((job.getStartDate().getDayOfWeek().getValue() - 5), (job.getEndDate().getDayOfWeek().getValue() - 5))))
//                 .asConstraint("Overlaps weekend");
//     }

    // ************************************************************************
    // Medium constraints
    // ************************************************************************

    public Constraint nullCrew(ConstraintFactory constraintFactory) {
               return constraintFactory.forEachIncludingNullVars(Job.class)
               .filter(job -> job.getCrew() == null)
               .penalizeLong(HardMediumSoftLongScore.ONE_MEDIUM,
                        job -> 1L)
                .asConstraint("No suitable crew for job");
    }


    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    public Constraint insideWorkHours(ConstraintFactory constraintFactory) {
        // Preferably only work between 7AM en 4PM
        return constraintFactory
                .forEach(Job.class)
                        .filter(job -> job.getStartDate().toLocalTime() != null
                                &&
                                        // Don't start the job before 7AM or after 4PM
                                        ((job.getStartDate().toLocalTime().isBefore(LocalTime.of(7, 0, 0))
                                        || job.getStartDate().toLocalTime().isAfter(LocalTime.of(16, 0, 0)))
                                ||
                                        // Don't end it after 4PM or before 7AM
                                        (job.getEndDate().toLocalTime().isAfter(LocalTime.of(16, 0, 0))
                                        || job.getEndDate().toLocalTime().isBefore(LocalTime.of(7, 0, 0))))
                                )
                .penalizeLong(HardMediumSoftLongScore.ONE_SOFT,
                        job -> job.getStartDate().toLocalTime().compareTo(LocalTime.of(7, 0, 0)) < 0
                                ? ChronoUnit.MINUTES.between(job.getStartDate().toLocalTime(), LocalTime.of(7, 0, 0))
                                : job.getEndDate().toLocalTime().compareTo(LocalTime.of(16, 0, 0)) > 0
                                        ? ChronoUnit.MINUTES.between(LocalTime.of(16, 0, 0), job.getEndDate().toLocalTime())
                                        : ChronoUnit.MINUTES.between(job.getEndDate().toLocalTime(), LocalTime.of(7, 0, 0))
                        )
                .asConstraint("Before work hours");
    }

}