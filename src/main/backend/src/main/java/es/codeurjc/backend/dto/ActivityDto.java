package es.codeurjc.backend.dto;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.sql.Blob;

public record ActivityDto (
    Long id,
    String name,
    String category,
    String description,
    int vacancy,
    Calendar creationDate,
    String formattedCreationDate,
    Blob imageFile,
    Date activityDate,

    PlaceDto place,
    List<ReviewDto> reviews,
    
    
    boolean imageBoolean
)



{
    
}

