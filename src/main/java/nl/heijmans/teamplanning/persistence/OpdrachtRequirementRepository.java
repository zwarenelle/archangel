package nl.heijmans.teamplanning.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import nl.heijmans.teamplanning.domain.OpdrachtRequirement;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class OpdrachtRequirementRepository implements PanacheRepository<OpdrachtRequirement> {

}