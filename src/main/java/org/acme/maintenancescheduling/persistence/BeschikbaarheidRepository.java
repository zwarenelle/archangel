package org.acme.maintenancescheduling.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.maintenancescheduling.domain.Beschikbaarheid;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class BeschikbaarheidRepository implements PanacheRepository<Beschikbaarheid> {

}