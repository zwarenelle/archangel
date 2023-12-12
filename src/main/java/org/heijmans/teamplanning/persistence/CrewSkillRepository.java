package org.heijmans.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.heijmans.teamplanning.domain.CrewSkill;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class CrewSkillRepository implements PanacheRepository<CrewSkill> {

}