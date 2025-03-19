package es.codeurjc.backend.dto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import es.codeurjc.backend.model.Activity;
import es.codeurjc.backend.model.Place;
import es.codeurjc.backend.model.Review;
import es.codeurjc.backend.model.User;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

    ActivityMapper INSTANCE = Mappers.getMapper(ActivityMapper.class);

    @Mapping(target = "place", source = "place") // Mapea el lugar
    @Mapping(target = "reviews", source = "reviews") // Mapea las reseñas
    ActivityDto toDto(Activity activity);

    List<ActivityDto> toDTOs(Collection<Activity> activities);

    @Mapping(target = "id", ignore = true)
    void updateActivityFromDto(ActivityUpdateDto dto, @MappingTarget Activity activity);

    // Método para mapear Place a PlaceDto
    default PlaceDto mapPlace(Place place) {
        if (place == null) {
            return null;
        }
        return new PlaceDto(place.getId(), place.getName(), place.getDescription());
    }


    // Método para mapear una Review individual
    default ReviewDto mapReview(Review review) {
        if (review == null) {
            return null;
        }
        return new ReviewDto(
            review.getId(),
            review.getDescription(),
            review.getStarsValue(),
            review.getCreationDate(),
            getUserFullName(review.getUser()) // Obtiene el nombre y apellidos del usuario
        );
    }

    // Método para obtener el nombre y apellidos del usuario
    default String getUserFullName(User user) {
        if (user == null) {
            return null;
        }
        return user.getName() + " " + user.getSurname(); // Concatena nombre y apellidos
    }


}