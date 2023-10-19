package org.acme.maintenancescheduling.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.maintenancescheduling.domain.Skill;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class SkillRepository implements PanacheRepository<Skill> {

}