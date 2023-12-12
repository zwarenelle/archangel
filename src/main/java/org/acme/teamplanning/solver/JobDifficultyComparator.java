package org.acme.teamplanning.solver;

import java.util.Comparator;

import org.acme.teamplanning.domain.Job;

public class JobDifficultyComparator implements Comparator<Job> {
    
    @Override
    public int compare(Job a, Job b) {
        return a.getDurationInMinutes() - b.getDurationInMinutes();
    }

}