package nl.heijmans.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import nl.heijmans.teamplanning.domain.Opdracht;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class OpdrachtRepository implements PanacheRepository<Opdracht> {

}