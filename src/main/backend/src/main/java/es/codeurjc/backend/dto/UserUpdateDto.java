package es.codeurjc.backend.dto;

public record UserUpdateDto (
    String name,
    String surname,
    String phone,
    String dni,
    boolean imageBoolean
) {}

