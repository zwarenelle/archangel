package org.heijmans.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.heijmans.teamplanning.domain.Beschikbaarheid;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class BeschikbaarheidRepository implements PanacheRepository<Beschikbaarheid> {

}