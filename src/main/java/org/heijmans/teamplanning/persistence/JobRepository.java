package org.heijmans.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.heijmans.teamplanning.domain.Job;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class JobRepository implements PanacheRepository<Job> {

}