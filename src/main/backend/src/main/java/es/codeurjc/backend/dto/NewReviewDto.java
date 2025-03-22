package es.codeurjc.backend.dto;

public record NewReviewDto(
    int starsValue,
    String comment,
    Long userId
) {}