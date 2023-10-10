package org.acme.maintenancescheduling.bootstrap;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
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

    @ConfigProperty(name = "schedule.demoData", defaultValue = "SMALL")
    public DemoData demoData;

    public enum DemoData {
        NONE,
        SMALL,
        LARGE
    }

    @Inject
    WorkCalendarRepository workCalendarRepository;
    @Inject
    CrewRepository crewRepository;
    @Inject
    JobRepository jobRepository;

    @Transactional
    public void generateDemoData(@Observes StartupEvent startupEvent) {
        if (demoData == DemoData.NONE) {
            return;
        }

        List<Crew> crewList = new ArrayList<>();
        crewList.add(new Crew("Ploeg E1", "Elektra"));
        crewList.add(new Crew("Ploeg E2", "Elektra"));
        crewList.add(new Crew("Ploeg G1", "Gas"));
        crewList.add(new Crew("Ploeg G2", "Gas"));
        crewRepository.persist(crewList);

        LocalDateTime fromDate = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).with(LocalTime.of(0, 0, 0, 0));
        int weekListSize = (demoData == DemoData.LARGE) ? 16 : 8;
        LocalDateTime toDate = fromDate.plusWeeks(weekListSize);
        workCalendarRepository.persist(new WorkCalendar(fromDate, toDate));
        int workdayTotal = weekListSize * 5;

        final String[] JOB_AREA_NAMES = {
                "Spui", "Raamsteeg", "Rokin", "Damrak", "Kalverstraat", "Nieuwmarkt", "Nieuwmarkt", "Spooksteeg", "Oudezijds Voorburgwal",
                "Geldersekade", "Kromme Waal", "Singel", "Keizersgracht", "Rozengracht", "Prinsengracht", "Halvemaansteeg", "Kerkstraat", "Korte Prinsengracht"};
        final String[] JOB_TARGET_NAMES = {"Amsterdam", "Landsmeer", "Amstelveen"};

        List<Job> jobList = new ArrayList<>();
        int jobListSize = weekListSize * crewList.size() * 3 / 5;
        int jobAreaTargetLimit = Math.min(JOB_TARGET_NAMES.length, crewList.size() * 2);
        Random random = new Random(17);
        for (int i = 0; i < jobListSize; i++) {
            String jobArea = JOB_AREA_NAMES[i / jobAreaTargetLimit];
            String jobTarget = JOB_TARGET_NAMES[i % jobAreaTargetLimit];
            // 1 day to 2 workweeks (1 workweek on average)
            int durationInDays = 1 + random.nextInt(4);
            int readyDueBetweenWorkdays = durationInDays + 5 // at least 5 days of flexibility
                    + random.nextInt(workdayTotal - (durationInDays + 5));
            int readyWorkdayOffset = random.nextInt(workdayTotal - readyDueBetweenWorkdays + 1);
            int readyIdealEndBetweenWorkdays = readyDueBetweenWorkdays - 1 - random.nextInt(4);
            LocalDateTime readyDate = EndDateUpdatingVariableListener.calculateEndDate(fromDate, readyWorkdayOffset);
            LocalDateTime dueDate = EndDateUpdatingVariableListener.calculateEndDate(readyDate, readyDueBetweenWorkdays);
            LocalDateTime idealEndDate = EndDateUpdatingVariableListener.calculateEndDate(readyDate, readyIdealEndBetweenWorkdays);
            // Some have both tags
            // Set<String> tagSet = random.nextDouble() < 0.1 ? Set.of("Gas", "Elektra") : 
            //             random.nextInt(2) < 1 ? Set.of("Elektra") : Set.of("Gas");
            
            // Single tag
            Set<String> tagSet = random.nextInt(2) < 1 ? Set.of("Elektra") : Set.of("Gas");
            
            jobList.add(new Job(jobArea + " " + jobTarget, durationInDays, readyDate, dueDate, idealEndDate, tagSet));
        }

        jobRepository.persist(jobList);
    }

}