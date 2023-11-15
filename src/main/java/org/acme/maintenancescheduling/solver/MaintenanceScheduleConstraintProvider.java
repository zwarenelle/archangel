package org.acme.maintenancescheduling.solver;

import static ai.timefold.solver.core.api.score.stream.Joiners.equal;
import static ai.timefold.solver.core.api.score.stream.Joiners.overlapping;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.acme.maintenancescheduling.domain.Availability;
import org.acme.maintenancescheduling.domain.AvailabilityType;
import org.acme.maintenancescheduling.domain.Crew;
import org.acme.maintenancescheduling.domain.CrewSkills;
import org.acme.maintenancescheduling.domain.Job;
import org.acme.maintenancescheduling.domain.Monteur;

import ai.timefold.solver.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
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
                .join(Availability.class, Joiners.equal((Job Job) -> Job.getStartDate().toLocalDate(), Availability::getDate))
                        .filter((job, availability) -> job.getCrew() != null &&
                                !(job.getrequiredSkills().stream()
                                .allMatch(crewskill -> job.getCrew().getCrewSkills().stream()
                                .anyMatch(jobreq -> 
                                (jobreq.getTypenummer() <= 3 ? 
                                crewskill.getTypenummer() <= jobreq.getTypenummer() : 
                                crewskill.getTypenummer() > 3 && crewskill.getTypenummer() <= jobreq.getTypenummer())
                                && crewskill.getAantal() <= jobreq.getAantal())))
                        )
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        (job, availability) -> 1L)
                .asConstraint("Skill Conflict");
    }

    public Constraint Availability(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Job.class)
                .filter(job -> job.getCrew() != null)
                        .join(Availability.class, Joiners.equal((Job Job) -> Job.getStartDate().toLocalDate(), Availability::getDate))
                        .filter((job, availability) -> !(job.getrequiredSkills().stream()
                        .allMatch(crewskill ->
                                reEvaluateCrewSkills(job.getCrew(), job.getCrew().getMonteurs().stream()
                                        .filter(monteur -> monteur.getId() == availability.getMonteur().getId() && availability.getAvailabilityType() == AvailabilityType.UNAVAILABLE)
                                        .collect(Collectors.toList())).stream()
                                .anyMatch(jobreq -> 
                                (jobreq.getTypenummer() <= 3 ? 
                                        crewskill.getTypenummer() <= jobreq.getTypenummer()
                                        :  
                                        crewskill.getTypenummer() > 3 && crewskill.getTypenummer() <= jobreq.getTypenummer()) && crewskill.getAantal() <= jobreq.getAantal())))
                        )
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        (job, availability) -> job.getCrew().getMonteurs().stream()
                                .filter(monteur -> monteur.getId() == availability.getMonteur().getId())
                                .count())
                .asConstraint("Unavailable employee");
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


    private static List<CrewSkills> removeTypes(List<CrewSkills> skills, List<Integer> typenummers) {
        for (Integer integer : typenummers) {
                for (CrewSkills skill : skills) {
                        int skillcount = skill.getAantal();
                        if (skill.getTypenummer() == integer && skillcount > 1) {
                                skill.setAantal(skillcount--);
                        }
                        else if (skill.getTypenummer() == integer && skillcount <= 1) {
                                skills.remove(skill);
                        }
                }
        }
        return skills;
    }

    private static List<CrewSkills> reEvaluateCrewSkills(Crew crew, List<Monteur> monteurs) {
        List<CrewSkills> redefinedskills = new ArrayList<CrewSkills>();
        
        List<Monteur> newMonteurList = new ArrayList<Monteur>(crew.getMonteurs());
        newMonteurList.removeAll(monteurs);

        redefinedskills = newMonteurList.stream()
        .map((monteur) -> new CrewSkills(monteur.getVaardigheid().getTypenummer(), 1, monteur.getVaardigheid().getOmschrijving()))
        .collect(Collectors.toList());

        // Sort list by typenummer, just to be sure
        redefinedskills.sort(Comparator.comparing(CrewSkills::getTypenummer));

        // Group entry's with the same typenummer into a map
        Map<Integer, List<CrewSkills>> skillMap = redefinedskills.stream()
        .collect(Collectors.groupingBy(crewskill -> crewskill.getTypenummer()));

        // Create new list including typenummmer count
        List<CrewSkills> skillsummary = new ArrayList<CrewSkills>();

        for (Map.Entry<Integer, List<CrewSkills>> crewskill : skillMap.entrySet()) {
            skillsummary.add(new CrewSkills(crewskill.getKey(), crewskill.getValue().size(), crewskill.getValue().iterator().next().getOmschrijving()));
        }
        
        return skillsummary;
    }

}