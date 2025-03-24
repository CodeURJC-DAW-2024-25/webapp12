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
    @Mapping(target = "creationDate", source = "creationDate")
    @Mapping(target = "place", source = "place") 
    @Mapping(target = "reviews", source = "reviews")
    ActivityDto toDto(Activity activity);
    List<ActivityDto> toDTOs(Collection<Activity> activities);
    @Mapping(target = "id", ignore = true)
    void updateActivityFromDto(NewActivityDto dto, @MappingTarget Activity activity);

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
}