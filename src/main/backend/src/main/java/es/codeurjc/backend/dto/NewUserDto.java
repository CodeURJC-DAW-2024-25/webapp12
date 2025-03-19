package es.codeurjc.backend.dto;

import java.util.List;

public record NewUserDto (
    String name,          
    String surname,       
    String email,         
    String phone,         
    String dni,           
    String password,      
    List<String> roles,   
    boolean imageBoolean  
) {}

