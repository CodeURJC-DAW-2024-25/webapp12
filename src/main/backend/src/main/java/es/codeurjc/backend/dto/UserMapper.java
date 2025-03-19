package es.codeurjc.backend.dto;
import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import es.codeurjc.backend.model.User;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);
    List<UserDto> toDTOs(Collection<User> users);

    @Mapping(target = "id", ignore = true) 
    @Mapping(target = "email", ignore = true) 
    @Mapping(target = "roles", ignore = true) 
    @Mapping(target = "password", ignore = true)

    @Mapping(target = "imageFile", ignore = true)
    User toDomain(NewUserDto newUserDto);
    void updateUserFromDto(UserUpdateDto dto, @MappingTarget User user);
}
