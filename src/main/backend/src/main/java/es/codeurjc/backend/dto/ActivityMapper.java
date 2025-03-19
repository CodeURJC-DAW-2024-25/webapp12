package es.codeurjc.backend.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import es.codeurjc.backend.model.Activity;


@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityDto toDto(Activity activity);
    List<ActivityDto> toDTOs(Collection<Activity> activities);

    @Mapping(target = "id", ignore = true) 
    void updateActivityFromDto(ActivityUpdateDto dto, @MappingTarget Activity activity);
}
