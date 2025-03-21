package es.codeurjc.backend.rest;

import es.codeurjc.backend.dto.ActivitiesByMonthDto;
import es.codeurjc.backend.dto.GeneralStatisticsDto;
import es.codeurjc.backend.dto.ReviewStatisticsDto;
import es.codeurjc.backend.service.ActivityService;
import es.codeurjc.backend.service.PlaceService;
import es.codeurjc.backend.service.ReviewService;
import es.codeurjc.backend.service.UserService;
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

    // Endpoint para obtener la cantidad de actividades realizadas cada mes
    @GetMapping("/activities-by-month")
    public ResponseEntity<List<ActivitiesByMonthDto>> getActivitiesByMonth() {
        Map<Integer, Long> activitiesByMonth = activityService.countActivitiesByMonth();

        // Convertir el mapa a una lista de DTOs
        List<ActivitiesByMonthDto> result = activitiesByMonth.entrySet().stream()
                .map(entry -> new ActivitiesByMonthDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // Endpoint para obtener la puntuación total de las valoraciones realizadas por los usuarios
    @GetMapping("/review-statistics")
    public ResponseEntity<List<ReviewStatisticsDto>> getReviewStatistics() {
        Map<Integer, Long> reviewData = reviewService.countReviewsByValorationDto();

        // Convertir el mapa a una lista de DTOs
        List<ReviewStatisticsDto> result = reviewData.entrySet().stream()
                .map(entry -> new ReviewStatisticsDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // Endpoint para obtener las estadísticas generales (usuarios, actividades y lugares)
    @GetMapping("/general-statistics")
    public ResponseEntity<GeneralStatisticsDto> getGeneralStatistics() {
        long userCount = userService.countUsers();
        long activityCount = activityService.activityCount();
        long placeCount = placeService.placeCount();

        GeneralStatisticsDto result = new GeneralStatisticsDto(userCount, activityCount, placeCount);
        return ResponseEntity.ok(result);
    }
}

