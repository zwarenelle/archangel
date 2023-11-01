package org.acme.maintenancescheduling.bootstrap;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.acme.maintenancescheduling.domain.Crew;
import org.acme.maintenancescheduling.domain.Job;
import org.acme.maintenancescheduling.domain.JobRequirement;
import org.acme.maintenancescheduling.domain.Monteur;
import org.acme.maintenancescheduling.domain.Skill;
import org.acme.maintenancescheduling.domain.WorkCalendar;
import org.acme.maintenancescheduling.persistence.CrewRepository;
import org.acme.maintenancescheduling.persistence.JobRepository;
import org.acme.maintenancescheduling.persistence.JobRequirementRepository;
import org.acme.maintenancescheduling.persistence.MonteurRepository;
import org.acme.maintenancescheduling.persistence.WorkCalendarRepository;
import org.acme.maintenancescheduling.solver.EndDateUpdatingVariableListener;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DemoDataGenerator {

    @ConfigProperty(name = "schedule.demoData", defaultValue = "TRUE")
    public DemoData demoData;

    public enum DemoData {
        FALSE,
        TRUE
    }

    @Inject
    WorkCalendarRepository workCalendarRepository;
    @Inject
    CrewRepository crewRepository;
    @Inject
    JobRepository jobRepository;
    @Inject
    JobRequirementRepository skillRepository;
    @Inject
    MonteurRepository monteurRepository;

    @Transactional
    public void generateDemoData(@Observes StartupEvent startupEvent) {
        if (demoData == DemoData.FALSE) {
            return;
        }
        
        // Ploeg COMBI
        // new Monteur("Paul", new Skill(1, "VIAG VP")), new Monteur("Robbert", new Skill(3, "VIAG VOP")), new Monteur("Marichelle", new Skill(4, "BEI VP")), new Monteur("Fons", new Skill(6, "BEI VOP"))

        // Ploeg E 1+2
        // new Monteur("Emiel", new Skill(4, "BEI VP")), new Monteur("Mark", new Skill(6, "BEI VOP"))
        // new Monteur("Gijs", new Skill(4, "BEI VP")), new Monteur("Dave", new Skill(6, "BEI VOP"))

        // Ploeg G 1+2
        // new Monteur("Tom", new Skill(1, "VIAG VP")), new Monteur("Bas", new Skill(3, "VIAG VOP"))
        // new Monteur("John", new Skill(1, "VIAG VP")), new Monteur("Mike", new Skill(3, "VIAG VOP"))
        
        List<Crew> crewList = new ArrayList<>();
        
        crewList.add(new Crew("Ploeg COMBI", List.of(new Monteur("Paul", new Skill(1, "VIAG VP")), new Monteur("Robbert", new Skill(3, "VIAG VOP")), new Monteur("Marichelle", new Skill(4, "BEI VP")), new Monteur("Fons", new Skill(6, "BEI VOP")))));
        crewList.add(new Crew("Ploeg E1", List.of(new Monteur("Emiel", new Skill(4, "BEI VP")), new Monteur("Mark", new Skill(6, "BEI VOP")))));
        crewList.add(new Crew("Ploeg E2", List.of(new Monteur("Gijs", new Skill(4, "BEI VP")), new Monteur("Dave", new Skill(6, "BEI VOP")))));
        crewList.add(new Crew("Ploeg G1", List.of(new Monteur("Tom", new Skill(1, "VIAG VP")), new Monteur("Bas", new Skill(3, "VIAG VOP")))));
        crewList.add(new Crew("Ploeg G2", List.of(new Monteur("John", new Skill(1, "VIAG VP")), new Monteur("Mike", new Skill(3, "VIAG VOP")), new Monteur("Mike2", new Skill(3, "VIAG VOP")))));
        
        crewRepository.persist(crewList);

        final String[] JOB_AREA_NAMES = {
                "Spui", "Raamsteeg", "Rokin", "Damrak", "Kalverstraat", "Nieuwmarkt", "Nieuwmarkt", "Spooksteeg", "Oudezijds Voorburgwal",
                "Geldersekade", "Kromme Waal", "Singel", "Keizersgracht", "Rozengracht", "Prinsengracht", "Halvemaansteeg", "Kerkstraat", "Korte Prinsengracht"};

        LocalDateTime fromDate = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).with(LocalTime.of(0, 0, 0, 0));
        int weekListSize = 4;
        LocalDateTime toDate = fromDate.plusWeeks(weekListSize);
        workCalendarRepository.persist(new WorkCalendar(fromDate, toDate));
        int workdayTotal = weekListSize * 5;

        List<Job> jobList = new ArrayList<>();
        int jobListSize = crewList.size() * 15;

        Random random = new Random(17);
        for (int i = 0; i < jobListSize; i++) {
            String jobArea = JOB_AREA_NAMES[random.nextInt(JOB_AREA_NAMES.length)];

            int durationInDays = 1;
            int durationInHours = 4;
            
            int readyDueBetweenWorkdays = durationInDays + 5 // at least 5 days of flexibility
                    + random.nextInt(workdayTotal - (durationInDays + 5));
            int readyWorkdayOffset = random.nextInt(workdayTotal - readyDueBetweenWorkdays + 1);
            int readyIdealEndBetweenWorkdays = readyDueBetweenWorkdays - 1 - random.nextInt(2);
            LocalDateTime readyDate = EndDateUpdatingVariableListener.calculateEndDate(fromDate, readyWorkdayOffset * 24);
            LocalDateTime dueDate = EndDateUpdatingVariableListener.calculateEndDate(readyDate, readyDueBetweenWorkdays * 24);
            LocalDateTime idealEndDate = EndDateUpdatingVariableListener.calculateEndDate(readyDate, readyIdealEndBetweenWorkdays * 24);

            List<JobRequirement> requiredSkills = random.nextDouble() < 0.1 ?
                List.of(new JobRequirement(4, 1, "BEI VP"), new JobRequirement(6, 1, "BEI VOP"), new JobRequirement(1, 1, "VIAG VP")) : 
                random.nextInt(2) < 1 ?
                List.of(new JobRequirement(4, 1, "BEI VP"), new JobRequirement(6, 1, "BEI VOP")) :
                List.of(new JobRequirement(1, 1, "VIAG VP"), new JobRequirement(3, 1, "VIAG VOP"));

            if (requiredSkills.size() > 2) {
                jobArea += " C";
            }
            jobList.add(new Job(jobArea, "E0001", durationInDays, durationInHours, readyDate, dueDate, idealEndDate, requiredSkills));
        }

        jobRepository.persist(jobList);
    }

}