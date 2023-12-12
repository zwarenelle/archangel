package nl.heijmans.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import nl.heijmans.teamplanning.domain.Job;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class JobRepository implements PanacheRepository<Job> {

}