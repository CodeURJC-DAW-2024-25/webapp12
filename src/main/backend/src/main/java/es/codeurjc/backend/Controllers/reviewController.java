package es.codeurjc.backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.backend.Service.ReviewService;

@RequestMapping("/activity")
public class reviewController {
    
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/activity/{activityId}/addReview")
    public String addReview(@PathVariable Long activityId, 
                            @RequestParam int starsValue, 
                            @RequestParam String description,
                            @RequestParam Long userId
                            ) {
        try {
            reviewService.saveReview(activityId, starsValue, description, userId);
            return "redirect:/activity/" + activityId;  // Redirigir a la página de actividad
        } catch (Exception e) {
            return "Error al agregar la review: " + e.getMessage();
        }
    }



    
    
}

