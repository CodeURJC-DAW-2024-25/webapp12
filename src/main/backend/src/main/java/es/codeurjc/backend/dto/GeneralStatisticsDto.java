package es.codeurjc.backend.dto;

public record GeneralStatisticsDto(
    long userCount,
    long activityCount,
    long placeCount
) {}
