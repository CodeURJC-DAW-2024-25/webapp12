package es.codeurjc.backend.Controllers;



import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.backend.Model.User;
import es.codeurjc.backend.Repository.UserRepository;
import es.codeurjc.backend.Service.ReviewService;

@Controller
public class reviewController {
    
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/activity/{activityId}/addReview")
    public String addReview(@PathVariable Long activityId, 
                            @RequestParam int starsValue, 
                            @RequestParam String description,
                            @RequestParam (required = false)Long userId,
                            Principal principal
                            ) 
        {
        String username = principal.getName();
        User user = userRepository.findByEmail(username);
        if (userId == null) {
            userId = user.getId();
        }
        
        try {
            reviewService.saveReview(activityId, starsValue, description, userId);
            return "redirect:/activity/" + activityId;  // Redirigir a la página de actividad
        } catch (Exception e) {
            return "Error al agregar la review: " + e.getMessage();
        }
    }



    
    
}

