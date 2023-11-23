package org.acme.maintenancescheduling.solver;

import java.util.Comparator;

import org.acme.maintenancescheduling.domain.Job;

public class JobDifficultyComparator implements Comparator<Job> {
    
    @Override
    public int compare(Job a, Job b) {
        return a.getdurationInHours() - b.getdurationInHours();
    }

}