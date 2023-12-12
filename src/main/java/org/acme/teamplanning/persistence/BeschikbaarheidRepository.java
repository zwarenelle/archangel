package org.acme.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.teamplanning.domain.Beschikbaarheid;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class BeschikbaarheidRepository implements PanacheRepository<Beschikbaarheid> {

}