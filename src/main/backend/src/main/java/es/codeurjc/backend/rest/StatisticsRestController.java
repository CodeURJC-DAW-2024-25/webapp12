package es.codeurjc.backend.rest;

import es.codeurjc.backend.dto.ActivitiesByMonthDto;
import es.codeurjc.backend.dto.GeneralStatisticsDto;
import es.codeurjc.backend.dto.ReviewStatisticsDto;
import es.codeurjc.backend.service.ActivityService;
import es.codeurjc.backend.service.PlaceService;
import es.codeurjc.backend.service.ReviewService;
import es.codeurjc.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsRestController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    @Operation(summary = "Get the activities-by-month", description = "Returns the number of activities created per month by the administrator")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Activities-by-month returned successfully", content = @Content),
		@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
		@ApiResponse(responseCode = "404", description = "User not found", content = @Content)
	})
    @GetMapping("/activities-by-month")
    public ResponseEntity<List<ActivitiesByMonthDto>> getActivitiesByMonth() {
        Map<Integer, Long> activitiesByMonth = activityService.countActivitiesByMonth();

        // Convertir el mapa a una lista de DTOs
        List<ActivitiesByMonthDto> result = activitiesByMonth.entrySet().stream()
                .map(entry -> new ActivitiesByMonthDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get the review-statistics", description = "Returns the review-statistics of the the activity ratings")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Review-statistics returned successfully", content = @Content),
		@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
		@ApiResponse(responseCode = "404", description = "User not found", content = @Content)
	})
    @GetMapping("/review-statistics")
    public ResponseEntity<List<ReviewStatisticsDto>> getReviewStatistics() {
        Map<Integer, Long> reviewData = reviewService.countReviewsByValorationDto();

        
        List<ReviewStatisticsDto> result = reviewData.entrySet().stream()
                .map(entry -> new ReviewStatisticsDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get the general statistics", description = "Returns the general statistics of the user, activity and place count")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "General statistics returned successfully", content = @Content),
		@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
		@ApiResponse(responseCode = "404", description = "User not found", content = @Content)
	})
    @GetMapping("/general-statistics")
    public ResponseEntity<GeneralStatisticsDto> getGeneralStatistics() {
        long userCount = userService.countUsers();
        long activityCount = activityService.activityCount();
        long placeCount = placeService.placeCount();

        GeneralStatisticsDto result = new GeneralStatisticsDto(userCount, activityCount, placeCount);
        return ResponseEntity.ok(result);
    }
}

