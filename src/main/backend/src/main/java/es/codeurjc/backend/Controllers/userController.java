package es.codeurjc.backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.backend.Service.UserService;
import es.codeurjc.backend.Service.ActivityService;

import org.springframework.ui.Model;

import es.codeurjc.backend.Model.Activity;
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
    
    
    

}
