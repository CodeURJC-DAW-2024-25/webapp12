package es.codeurjc.backend.dto;

import java.util.Calendar;
import java.util.Date;

public record ActivityUpdateDto (
    String name,
    String category,
    String description,
    boolean imageBoolean,
    int vacancy,
    Calendar creationDate,
    Date activityDate,
    Long placeId
    
) {}
