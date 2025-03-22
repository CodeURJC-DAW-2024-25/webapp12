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


@RestController
@RequestMapping("/api/reviews")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Page<ReviewDto>> getReviewsByActivity(
            @PathVariable Long activityId,
            @RequestParam(defaultValue = "0") int page) { 

        Page<ReviewDto> reviewsPage = reviewService.getReviewsPaginatedDto(activityId, page);

        return ResponseEntity.ok(reviewsPage);
    }

    @PostMapping("/activity/{activityId}")
    public ResponseEntity<?> addReview(
            @PathVariable Long activityId,
            @RequestBody NewReviewDto newReviewDto,
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

    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @RequestBody NewReviewDto newReviewDto,
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

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReviewById(
            @PathVariable Long reviewId) {
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