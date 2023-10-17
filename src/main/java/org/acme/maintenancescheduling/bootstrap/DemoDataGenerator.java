package org.acme.maintenancescheduling.bootstrap;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.acme.maintenancescheduling.domain.Crew;
import org.acme.maintenancescheduling.domain.Job;
import org.acme.maintenancescheduling.domain.WorkCalendar;
import org.acme.maintenancescheduling.persistence.CrewRepository;
import org.acme.maintenancescheduling.persistence.JobRepository;
import org.acme.maintenancescheduling.persistence.WorkCalendarRepository;
import org.acme.maintenancescheduling.solver.EndDateUpdatingVariableListener;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.StartupEvent;

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

    @Transactional
    public void generateDemoData(@Observes StartupEvent startupEvent) {
        if (demoData == DemoData.FALSE) {
            return;
        }

        List<Crew> crewList = new ArrayList<>();
        crewList.add(new Crew("Ploeg COMBI", List.of("Elektra", "Gas")));
        crewList.add(new Crew("Ploeg E1", List.of("Elektra")));
        crewList.add(new Crew("Ploeg E2", List.of("Elektra")));
        crewList.add(new Crew("Ploeg G1", List.of("Gas")));
        crewList.add(new Crew("Ploeg G2", List.of("Gas")));
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
            // Some have both tags
            // Set<String> requiredSkills = random.nextDouble() < 0.1 ? Set.of("Gas", "Elektra") : 
            //             random.nextInt(2) < 1 ? Set.of("Elektra") : Set.of("Gas");
            
            // Single tag
            List<String> requiredSkills = random.nextInt(2) < 1 ? List.of("Elektra") : List.of("Gas");
            
            jobList.add(new Job(jobArea, durationInDays, durationInHours, readyDate, dueDate, idealEndDate, requiredSkills));
        }

        jobRepository.persist(jobList);
    }

}