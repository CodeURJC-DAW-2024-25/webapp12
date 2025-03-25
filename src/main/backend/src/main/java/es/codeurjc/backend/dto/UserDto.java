package es.codeurjc.backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record UserDto(
    Long id,
    String name,
    String surname,
    String email,
    String phone,
    String dni,
    List<String> roles,
    boolean imageBoolean,
    @JsonIgnore
    String password 
) {}