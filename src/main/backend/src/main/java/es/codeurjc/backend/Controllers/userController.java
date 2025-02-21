package es.codeurjc.backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.backend.Service.UserService;
import jakarta.transaction.Transactional;
import es.codeurjc.backend.Service.ActivityService;
import es.codeurjc.backend.Service.ReviewService;


import org.springframework.ui.Model;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Model.Review;
import es.codeurjc.backend.Model.User;
import es.codeurjc.backend.Repository.UserRepository;
import java.util.List;
import java.util.Optional;

@Controller
public class userController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ActivityService activityService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/loginError")
    public String showErrorLogin() {
        return "404";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }
    
    @GetMapping("/admin_users")
    public String showAdminUsers(Model model) {
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("activityCount", activityService.activityCount());
        return "admin_users";
    }


    /*@GetMapping("/profile")
    public String showProfile(Model model, @RequestParam(value = "id", required = false) Long id) {
        if (id == null) {
           return "redirect:/login"; 
        }

        Optional<User> optionalUser  = userRepository.findById(id); 
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            List<Activity> subscribedActivities = activityService.findEventsSubscribe(user);
            model.addAttribute("user", user);
            model.addAttribute("subscribedActivities", subscribedActivities);
            model.addAttribute("countActivitiesSubscribed", subscribedActivities.size());
        }else{
          return "redirect:/404";
        }
        return "profile";
    }*/
    @Transactional
    @GetMapping("/deleteUser/{id}")
    public String removeUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();

            List<Activity> activities = user.getActivities();
            for(Activity activity:activities){
                activity.getUsers().remove(user);
                activityService.saveActivity(activity);
            }

            List<Review> reviews = user.getReviews();
            for(Review review:reviews){
                reviewService.delete(review.getId());
            }

            userRepository.delete(user);
            return "redirect:/admin_users";
        }else{
            return "404";
        }
        
    }
    
    

}
