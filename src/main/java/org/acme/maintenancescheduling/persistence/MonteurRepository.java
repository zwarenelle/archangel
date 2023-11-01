package org.acme.maintenancescheduling.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.maintenancescheduling.domain.Monteur;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class MonteurRepository implements PanacheRepository<Monteur> {

}
