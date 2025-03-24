package es.codeurjc.backend.dto;

import java.util.Calendar;

public record ReviewDto(
    Long id,
    String comment,
    int starsValue,
    Calendar creationDate,
    String userFullName
){}
