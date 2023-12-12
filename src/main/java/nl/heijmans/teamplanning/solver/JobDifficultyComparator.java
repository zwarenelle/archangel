package nl.heijmans.teamplanning.solver;

import java.util.Comparator;

import nl.heijmans.teamplanning.domain.Job;

public class JobDifficultyComparator implements Comparator<Job> {
    
    @Override
    public int compare(Job a, Job b) {
        return a.getDurationInMinutes() - b.getDurationInMinutes();
    }

}