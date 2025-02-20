package es.codeurjc.backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.backend.Service.UserService;

import org.springframework.ui.Model;



@Controller
public class userController {
    @Autowired
    private UserService userService;

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
        return "admin_users";
    }

    @GetMapping("/profile")
    public String showProfile() {
        return "profile";
    }
    
    

}
