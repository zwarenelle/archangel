package nl.heijmans.teamplanning.solver;

import java.time.LocalDateTime;

import nl.heijmans.teamplanning.domain.Opdracht;
import nl.heijmans.teamplanning.domain.Teamplanning;

import ai.timefold.solver.core.api.domain.variable.VariableListener;
import ai.timefold.solver.core.api.score.director.ScoreDirector;

public class EndDateUpdatingVariableListener implements VariableListener<Teamplanning, Opdracht> {

    @Override
    public void beforeEntityAdded(ScoreDirector<Teamplanning> scoreDirector, Opdracht opdracht) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<Teamplanning> scoreDirector, Opdracht opdracht) {
        updateEndDate(scoreDirector, opdracht);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<Teamplanning> scoreDirector, Opdracht opdracht) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector<Teamplanning> scoreDirector, Opdracht opdracht) {
        updateEndDate(scoreDirector, opdracht);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<Teamplanning> scoreDirector, Opdracht opdracht) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<Teamplanning> scoreDirector, Opdracht opdracht) {
        // Do nothing
    }

    protected void updateEndDate(ScoreDirector<Teamplanning> scoreDirector, Opdracht opdracht) {
        scoreDirector.beforeVariableChanged(opdracht, "endDate");
        opdracht.setEndDate(calculateEndDate(opdracht.getStartDate(), opdracht.getDurationInMinutes()));
        scoreDirector.afterVariableChanged(opdracht, "endDate");
    }

    public static LocalDateTime calculateEndDate(LocalDateTime startDate, int durationInMinutes) {
        if (startDate == null) {
            return null;
        } else {
            return startDate.plusMinutes(durationInMinutes);
        }
    }
}