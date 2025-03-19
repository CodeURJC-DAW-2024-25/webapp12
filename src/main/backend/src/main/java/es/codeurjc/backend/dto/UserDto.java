package es.codeurjc.backend.dto;

import java.util.List;

public record UserDto(
    Long id,
    String name,
    String surname,
    String email,
    String phone,
    String dni,
    List<String> roles,
    boolean imageBoolean,
    String password 
) {}