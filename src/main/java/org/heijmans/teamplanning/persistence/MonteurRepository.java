package org.heijmans.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.heijmans.teamplanning.domain.Monteur;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class MonteurRepository implements PanacheRepository<Monteur> {

}
