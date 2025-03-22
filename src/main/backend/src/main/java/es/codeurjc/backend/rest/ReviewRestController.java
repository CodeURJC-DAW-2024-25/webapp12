package es.codeurjc.backend.rest;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.backend.dto.NewReviewDto;
import es.codeurjc.backend.dto.ReviewDto;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.service.ReviewService;
import es.codeurjc.backend.service.UserService;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Controller", description = "Endpoints for managing reviews")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserService userService;

    @Operation(summary = "Get reviews by activity ID", description = "Returns a paginated list of reviews for a specific activity.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid activity ID supplied", content = @Content),
        @ApiResponse(responseCode = "404", description = "Activity not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Page<ReviewDto>> getReviewsByActivity(
            @Parameter(description = "ID of the activity to fetch reviews for", required = true) @PathVariable Long activityId,
            @Parameter(description = "Page number (starting from 0)", example = "0") @RequestParam(defaultValue = "0") int page) { 

        Page<ReviewDto> reviewsPage = reviewService.getReviewsPaginatedDto(activityId, page);

        return ResponseEntity.ok(reviewsPage);
    }

    @Operation(summary = "Add a review to an activity", description = "Adds a new review to the specified activity. Requires authentication.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review added successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated", content = @Content),
        @ApiResponse(responseCode = "404", description = "Activity or user not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/activity/{activityId}")
    public ResponseEntity<?> addReview(
            @Parameter(description = "ID of the activity to add the review to", required = true) @PathVariable Long activityId,
            @Parameter(description = "Review data to add", required = true) @RequestBody NewReviewDto newReviewDto,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }
    
        String username = principal.getName();
        User user = userService.findByEmail(username);
    
        Long userId = newReviewDto.userId() != null ? newReviewDto.userId() : user.getId();
    
        try {
            ReviewDto reviewDto = reviewService.saveReviewDto(activityId, newReviewDto.starsValue(), newReviewDto.comment(), userId);
            return ResponseEntity.ok(reviewDto); // Respuesta 200 OK con el DTO de la revisión
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al agregar la review: " + e.getMessage()); // Respuesta 500 con mensaje de error
        }
    }

    @Operation(summary = "Update a review", description = "Updates an existing review. Requires authentication and ownership of the review.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden - User does not own the review", content = @Content),
        @ApiResponse(responseCode = "404", description = "Review not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @Parameter(description = "ID of the review to update", required = true) @PathVariable Long reviewId,
            @Parameter(description = "Updated review data", required = true) @RequestBody NewReviewDto newReviewDto,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        String username = principal.getName();
        User user = userService.findByEmail(username);

        try {
            ReviewDto reviewDto = reviewService.updateReview(reviewId, newReviewDto.starsValue(), newReviewDto.comment(), user.getId());
            return ResponseEntity.ok(reviewDto); // Respuesta 200 OK 
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage()); // Respuesta 403 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la review: " + e.getMessage()); // Respuesta 500 
        }
    }

    @Operation(summary = "Delete a review", description = "Deletes an existing review by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Review not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReviewById(
            @Parameter(description = "ID of the review to delete", required = true) @PathVariable Long reviewId) {
        try {
            reviewService.deleteReviewById(reviewId);
            return ResponseEntity.ok("Revisión eliminada correctamente"); // Respuesta 200 OK 
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage()); // Respuesta 404
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la review: " + e.getMessage()); // Respuesta 500
        }
    }
}