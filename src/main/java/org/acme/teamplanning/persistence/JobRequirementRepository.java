package org.acme.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.teamplanning.domain.JobRequirement;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class JobRequirementRepository implements PanacheRepository<JobRequirement> {

}