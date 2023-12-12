package nl.heijmans.teamplanning.solver;

import java.time.LocalDateTime;

import nl.heijmans.teamplanning.domain.Job;
import nl.heijmans.teamplanning.domain.Teamplanning;

import ai.timefold.solver.core.api.domain.variable.VariableListener;
import ai.timefold.solver.core.api.score.director.ScoreDirector;

public class EndDateUpdatingVariableListener implements VariableListener<Teamplanning, Job> {

    @Override
    public void beforeEntityAdded(ScoreDirector<Teamplanning> scoreDirector, Job job) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<Teamplanning> scoreDirector, Job job) {
        updateEndDate(scoreDirector, job);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<Teamplanning> scoreDirector, Job job) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector<Teamplanning> scoreDirector, Job job) {
        updateEndDate(scoreDirector, job);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<Teamplanning> scoreDirector, Job job) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<Teamplanning> scoreDirector, Job job) {
        // Do nothing
    }

    protected void updateEndDate(ScoreDirector<Teamplanning> scoreDirector, Job job) {
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