package org.acme.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.teamplanning.domain.Crew;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class CrewRepository implements PanacheRepository<Crew> {

}