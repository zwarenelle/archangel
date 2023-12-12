package org.acme.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.teamplanning.domain.Agenda;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class AgendaRepository implements PanacheRepository<Agenda> {

}