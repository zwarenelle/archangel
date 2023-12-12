package org.heijmans.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.heijmans.teamplanning.domain.Agenda;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class AgendaRepository implements PanacheRepository<Agenda> {

}