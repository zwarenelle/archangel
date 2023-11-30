package org.acme.maintenancescheduling.solver;

import java.time.LocalDateTime;

import org.acme.maintenancescheduling.domain.Job;
import org.acme.maintenancescheduling.domain.MaintenanceSchedule;
import ai.timefold.solver.core.api.domain.variable.VariableListener;
import ai.timefold.solver.core.api.score.director.ScoreDirector;

public class EndDateUpdatingVariableListener implements VariableListener<MaintenanceSchedule, Job> {

    @Override
    public void beforeEntityAdded(ScoreDirector<MaintenanceSchedule> scoreDirector, Job job) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<MaintenanceSchedule> scoreDirector, Job job) {
        updateEndDate(scoreDirector, job);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<MaintenanceSchedule> scoreDirector, Job job) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector<MaintenanceSchedule> scoreDirector, Job job) {
        updateEndDate(scoreDirector, job);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<MaintenanceSchedule> scoreDirector, Job job) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<MaintenanceSchedule> scoreDirector, Job job) {
        // Do nothing
    }

    protected void updateEndDate(ScoreDirector<MaintenanceSchedule> scoreDirector, Job job) {
        scoreDirector.beforeVariableChanged(job, "endDate");
        job.setEndDate(calculateEndDate(job.getStartDate(), job.getDurationInMinutes()));
        scoreDirector.afterVariableChanged(job, "endDate");
    }

    public static LocalDateTime calculateEndDate(LocalDateTime startDate, int durationInMinutes) {
        if (startDate == null) {
            return null;
        } else {
            return startDate.plusMinutes(durationInMinutes);
        }
    }
}