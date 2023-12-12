package nl.heijmans.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import nl.heijmans.teamplanning.domain.Beschikbaarheid;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class BeschikbaarheidRepository implements PanacheRepository<Beschikbaarheid> {

}