package org.acme.maintenancescheduling.solver;

import static ai.timefold.solver.core.api.score.stream.Joiners.equal;
import static ai.timefold.solver.core.api.score.stream.Joiners.overlapping;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.acme.maintenancescheduling.domain.Availability;
import org.acme.maintenancescheduling.domain.AvailabilityType;
import org.acme.maintenancescheduling.domain.Job;

import ai.timefold.solver.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
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
                // readyDate(constraintFactory),
                // dueDate(constraintFactory),
                // noWeekends(constraintFactory),
                skillConflict(constraintFactory),
                // Availability(constraintFactory),

                // Soft constraints
                insideWorkHours(constraintFactory)
                // beforeIdealEndDate(constraintFactory),
                // afterIdealEndDate(constraintFactory)
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
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        (job1, job2) -> ChronoUnit.HOURS.between(
                                job1.getStartDate().isAfter(job2.getStartDate())
                                        ? job1.getStartDate() : job2.getStartDate(),
                                job1.getEndDate().isBefore(job2.getEndDate())
                                        ? job1.getEndDate() : job2.getEndDate())
                        )
                .asConstraint("Crew conflict");
    }

//     public Constraint readyDate(ConstraintFactory constraintFactory) {
//         // Don't start a maintenance job before its ready to start.
//         return constraintFactory
//                 .forEach(Job.class)
//                         .filter(job -> job.getReadyDate() != null
//                                 && job.getStartDate().isBefore(job.getReadyDate()))
//                 .penalizeLong(HardSoftLongScore.ONE_HARD,
//                                 job -> ChronoUnit.HOURS.between(job.getStartDate(), job.getReadyDate()))
//                 .asConstraint("Ready date");
//     }

//     public Constraint dueDate(ConstraintFactory constraintFactory) {
//         // Don't end a maintenance job after its due.
//         return constraintFactory
//                 .forEach(Job.class)
//                         .filter(job -> job.getDueDate() != null
//                                 && job.getEndDate().isAfter(job.getDueDate()))
//                 .penalizeLong(HardSoftLongScore.ONE_HARD,
//                                 job -> ChronoUnit.HOURS.between(job.getDueDate(), job.getEndDate()))
//                 .asConstraint("Due date");
//     }

//     public Constraint noWeekends(ConstraintFactory constraintFactory) {
//         // A crew does not work on weekends
//         return constraintFactory
//                 .forEach(Job.class)
//                         .filter(job -> job.getStartDate() != null
//                                 && (job.getStartDate().getDayOfWeek().getValue() > 5)
//                                 || (job.getEndDate().getDayOfWeek().getValue() > 5)
//                                 )
//                 .penalizeLong(HardSoftLongScore.ONE_HARD,
//                         job -> Long.valueOf(Math.max((job.getStartDate().getDayOfWeek().getValue() - 5), (job.getEndDate().getDayOfWeek().getValue() - 5))))
//                 .asConstraint("Overlaps weekend");
//     }

    public Constraint skillConflict(ConstraintFactory constraintFactory) {
        // TODO: Put in a dynamic penalize function based on count of missing skills
        // Match crewSkills to JobRequirements
        return constraintFactory
                .forEach(Job.class)
                .join(Availability.class, 
                        Joiners.equal((Job job) -> job.getStartDate().toLocalDate(), Availability::getDate),
                        Joiners.filtering((job, availability) -> job.getCrew().getMonteurs().contains(availability.getMonteur())))
                .groupBy((job, availability) -> job, ConstraintCollectors.toList((job, availability) -> availability))
                        .filter((job, availability) -> job.getCrew() != null &&
                                !(job.getrequiredSkills().stream()
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
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        (job, availability) -> 1L)
                .asConstraint("Skill / Availability Conflict");
    }

//     public Constraint Availability(ConstraintFactory constraintFactory) {
//         return constraintFactory
//                 .forEach(Availability.class)
//                 // Match monteur to availability
//                 .join(Monteur.class, Joiners.equal((Availability availability) -> availability.getMonteur().getId(), Monteur::getId))
//                 // Filter unavailable and sick employees
//                 .filter((availability, monteur) -> availability.getAvailabilityType() != AvailabilityType.AVAILABLE)
//                 // Join jobs on date of availability
//                 .join(Job.class, Joiners.equal((availability, monteur) -> availability.getDate(), (Job job) -> job.getStartDate().toLocalDate()))
//                 // Filter actual employees planned for the job
//                 .filter((availability, monteur, job) -> job.getCrew().getMonteurs().contains(monteur))
//                 .groupBy((availability, monteur, job) -> job, ConstraintCollectors.toList((availability, monteur, job) -> availability))
//                 .filter((job, availability) -> job.getCrew() != null &&
//                                 !(job.getrequiredSkills().stream()
//                                 .allMatch(jobreq -> job.getCrew().getCrewSkills().stream().filter(skill -> availability.stream().map(a -> a.getMonteur().getVaardigheid().getTypenummer()).collect(Collectors.toList()).contains(skill.getTypenummer())).collect(Collectors.toList()).stream()
//                                 .anyMatch(crewskill -> 
//                                 (crewskill.getTypenummer() <= 3 ? 
//                                 jobreq.getTypenummer() <= crewskill.getTypenummer() : 
//                                 jobreq.getTypenummer() > 3 && jobreq.getTypenummer() <= crewskill.getTypenummer())
//                                 && jobreq.getAantal() <= crewskill.getAantal())))
//                         )
//                 .penalizeLong(HardSoftLongScore.ONE_HARD,
//                         (job, availability) -> 1L)
//                 .asConstraint("Employee unavailable");
//     }

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
                .penalizeLong(HardSoftLongScore.ONE_SOFT,
                        job -> job.getStartDate().toLocalTime().compareTo(LocalTime.of(7, 0, 0)) < 0
                                ? ChronoUnit.MINUTES.between(job.getStartDate().toLocalTime(), LocalTime.of(7, 0, 0))
                                : job.getEndDate().toLocalTime().compareTo(LocalTime.of(16, 0, 0)) > 0
                                        ? ChronoUnit.MINUTES.between(LocalTime.of(16, 0, 0), job.getEndDate().toLocalTime())
                                        : ChronoUnit.MINUTES.between(job.getEndDate().toLocalTime(), LocalTime.of(7, 0, 0))
                        )
                .asConstraint("Before work hours");
    }

//     public Constraint beforeIdealEndDate(ConstraintFactory constraintFactory) {
//         // Early maintenance is expensive because the sooner maintenance is done, the sooner it needs to happen again.
//         return constraintFactory.forEach(Job.class)
//                 .filter(job -> job.getIdealEndDate() != null
//                         && job.getEndDate().isBefore(job.getIdealEndDate()))
//                 .penalizeLong(HardSoftLongScore.ofSoft(1),
//                         job -> ChronoUnit.HOURS.between(job.getEndDate(), job.getIdealEndDate()))
//                 .asConstraint("Before ideal end date");
//     }

//     public Constraint afterIdealEndDate(ConstraintFactory constraintFactory) {
//         // Late maintenance is risky because delays can push it over the due date.
//         return constraintFactory.forEach(Job.class)
//                 .filter(job -> job.getIdealEndDate() != null
//                         && job.getEndDate().isAfter(job.getIdealEndDate()))
//                 .penalizeLong(HardSoftLongScore.ofSoft(10),
//                         job -> ChronoUnit.HOURS.between(job.getIdealEndDate(), job.getEndDate()))
//                 .asConstraint("After ideal end date");
//     }

}