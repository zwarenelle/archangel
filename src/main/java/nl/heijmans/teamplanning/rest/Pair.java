package nl.heijmans.teamplanning.rest;

import java.time.LocalDateTime;

import nl.heijmans.teamplanning.domain.Crew;

public record Pair(Crew crew, LocalDateTime startDate, LocalDateTime endDate) { }