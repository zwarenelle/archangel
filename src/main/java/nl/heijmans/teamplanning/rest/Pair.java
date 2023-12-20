package nl.heijmans.teamplanning.rest;

import java.time.LocalDateTime;

import nl.heijmans.teamplanning.domain.Ploeg;

public record Pair(Ploeg ploeg, LocalDateTime startDate, LocalDateTime endDate) { }