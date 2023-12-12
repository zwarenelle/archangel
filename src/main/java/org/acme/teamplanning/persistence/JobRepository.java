package org.acme.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.teamplanning.domain.Job;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class JobRepository implements PanacheRepository<Job> {

}