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
import es.codeurjc.backend.repository.PlaceRepository;
import jakarta.persistence.EntityNotFoundException;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

    ActivityMapper INSTANCE = Mappers.getMapper(ActivityMapper.class);
    
    @Mapping(target = "creationDate", source = "creationDate")
    @Mapping(target = "place", source = "place") 
    @Mapping(target = "reviews", source = "reviews")
    ActivityDto toDto(Activity activity);

    List<ActivityDto> toDTOs(Collection<Activity> activities);

    @Mapping(target = "id", ignore = true)
    void updateActivityFromDto(NewActivityDto dto, @MappingTarget Activity activity);

    // Método para mapear Place a PlaceDto
    default PlaceDto mapPlace(Place place) {
        if (place == null) {
            return null;
        }
        return new PlaceDto(place.getId(), place.getName(), place.getDescription());
    }

    default ReviewDto mapReview(Review review) {
        if (review == null) {
            return null;
        }
        return new ReviewDto(
            review.getId(),
            review.getDescription(),
            review.getStarsValue(),
            review.getCreationDate(),
            getUserFullName(review.getUser()) 
        );
    }

    default List<ReviewDto> mapReviews(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return List.of();
        }
        return reviews.stream()
            .map(this::mapReview)
            .collect(Collectors.toList());
    }

    default String getUserFullName(User user) {
        if (user == null) {
            return null;
        }
        return user.getName() + " " + user.getSurname();
    }
    
    default Activity toEntity(ActivityDto activityDto, PlaceRepository placeRepository) {
        if (activityDto == null) {
            return null;
        }

        Activity activity = new Activity();
        activity.setId(activityDto.id());
        activity.setName(activityDto.name());
        activity.setCategory(activityDto.category());
        activity.setDescription(activityDto.description());
        activity.setVacancy(activityDto.vacancy());
        activity.setCreationDate(activityDto.creationDate());
        
        // Convertir la fecha de actividad a java.sql.Date
        java.util.Date utilDate = activity.getActivityDate();
        if (utilDate != null) {
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            activity.setActivityDate(sqlDate);
        }

        activity.setImage(activityDto.imageBoolean());

        // Convertir PlaceDto a entidad Place
        PlaceDto placeDto = activityDto.place();
        if (placeDto != null) {
            Place place = placeRepository.findById(placeDto.id())
                    .orElseThrow(() -> new EntityNotFoundException("Lugar no encontrado con ID: " + placeDto.id()));
            activity.setPlace(place);
        }

        return activity;
    }




}