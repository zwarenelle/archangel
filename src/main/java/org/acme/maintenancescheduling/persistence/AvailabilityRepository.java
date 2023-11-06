package org.acme.maintenancescheduling.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.maintenancescheduling.domain.Availability;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class AvailabilityRepository implements PanacheRepository<Availability> {

}