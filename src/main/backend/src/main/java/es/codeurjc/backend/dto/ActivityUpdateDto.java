package es.codeurjc.backend.dto;

public record ActivityUpdateDto (
    String name,
    String category,
    String description,
    boolean imageBoolean   
    
) {}
