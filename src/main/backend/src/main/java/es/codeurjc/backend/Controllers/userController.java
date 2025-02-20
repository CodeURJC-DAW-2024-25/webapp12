package es.codeurjc.backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.backend.Service.UserService;
import es.codeurjc.backend.Service.ActivityService;

import org.springframework.ui.Model;



@Controller
public class userController {
    @Autowired
    private UserService userService;

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

    @GetMapping("/profile")
    public String showProfile() {
        return "profile";
    }
    
    

}
