package nl.heijmans.teamplanning.solver;

import java.util.Comparator;

import nl.heijmans.teamplanning.domain.Opdracht;

public class OpdrachtDifficultyComparator implements Comparator<Opdracht> {
    
    @Override
    public int compare(Opdracht a, Opdracht b) {
        return a.getDurationInMinutes() - b.getDurationInMinutes();
    }

}