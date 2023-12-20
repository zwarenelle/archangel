package nl.heijmans.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import nl.heijmans.teamplanning.domain.PloegSkill;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class PloegSkillRepository implements PanacheRepository<PloegSkill> {

}