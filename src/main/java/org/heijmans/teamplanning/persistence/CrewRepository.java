package org.heijmans.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.heijmans.teamplanning.domain.Crew;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class CrewRepository implements PanacheRepository<Crew> {

}