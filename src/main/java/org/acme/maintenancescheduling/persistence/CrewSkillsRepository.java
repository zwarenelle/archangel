package org.acme.maintenancescheduling.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.maintenancescheduling.domain.CrewSkills;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class CrewSkillsRepository implements PanacheRepository<CrewSkills> {

}