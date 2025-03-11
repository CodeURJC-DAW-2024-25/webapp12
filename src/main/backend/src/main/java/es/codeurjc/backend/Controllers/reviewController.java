package es.codeurjc.backend.controllers;



import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.backend.model.User;
import es.codeurjc.backend.service.ReviewService;
import es.codeurjc.backend.service.UserService;

@Controller
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @PostMapping("/activity/{activityId}/addReview")
    public String addReview(@PathVariable Long activityId, 
                            @RequestParam int starsValue, 
                            @RequestParam String description,
                            @RequestParam (required = false)Long userId,
                            Principal principal
                            ) 
        {
        String username = principal.getName();
        User user = userService.findByEmail(username);
        if (userId == null) {
            userId = user.getId();
        }
        
        try {
            reviewService.saveReview(activityId, starsValue, description, userId);
            return "redirect:/activity/" + activityId;  
        } catch (Exception e) {
            return "Error al agregar la review: " + e.getMessage();
        }
    }



    
    
}

