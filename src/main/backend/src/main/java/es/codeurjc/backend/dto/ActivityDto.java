package es.codeurjc.backend.dto;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public record ActivityDto (
    Long id,
    String name,
    String category,
    String description,
    int vacancy,
    Calendar creationDate,
    Date activityDate,

    PlaceDto place,
    List<ReviewDto> reviews,
    
    
    boolean imageBoolean
)



{
    
}

