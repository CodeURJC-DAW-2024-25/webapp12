package es.codeurjc.backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.backend.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import es.codeurjc.backend.Service.ActivityService;
import es.codeurjc.backend.Service.ReviewService;


import org.springframework.ui.Model;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Model.Review;
import es.codeurjc.backend.Model.User;
import es.codeurjc.backend.Repository.UserRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public String showAdminUsers(Model model,HttpServletRequest request) {
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("activityCount", activityService.activityCount());
        return "admin_users";
    }


    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal,HttpServletRequest request) {
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("user", request.isUserInRole("USER"));
        String userEmail = principal.getName();
        User user  = userRepository.findByEmail(userEmail); 
        
        System.out.println("email" + userEmail);
        if(user != null){
            List<Activity> subscribedActivities = activityService.findEventsSubscribe(user);
            model.addAttribute("userRegister", user);
            model.addAttribute("subscribedActivities", subscribedActivities);
            model.addAttribute("countActivitiesSubscribed", subscribedActivities.size());
            model.addAttribute("userCount", userService.countUsers());
            model.addAttribute("activityCount", activityService.activityCount());
            model.addAttribute("id", user.getId());
            return "profile";
        }else{
          return "redirect:/404";
        }
    }
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

    @GetMapping("/Edit_user-profile")
    public String showEditProfile(Principal principal,Model model,HttpServletRequest request) {
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("user", request.isUserInRole("USER"));
        String userEmail = principal.getName();
        User user = userRepository.findByEmail(userEmail);
        if(user != null){
            System.out.println(user.getDni());
            model.addAttribute("userRegistered", user);
            return "Edit_user-profile";
        }else{
            return "404";
        }   
    }

    @PostMapping("/Edit_user-profile")
    public String updateProfile(Model model, @ModelAttribute User userUpdated,  @RequestParam(required = false) String newPassword, 
    @RequestParam(required = false) String confirmPassword){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail);
            if(user!=null){
            user.setName(userUpdated.getName());
            user.setSurname(userUpdated.getSurname());
            user.setDni(userUpdated.getDni());
            user.setEmail(userUpdated.getEmail());
            user.setPhone(userUpdated.getPhone());
            if (newPassword != null && !newPassword.isEmpty()) {
                if (newPassword.equals(confirmPassword)) {
                    // Cifrar la nueva contraseña antes de guardarla
                    user.setPassword(passwordEncoder.encode(newPassword));
                } else {
                    model.addAttribute("error", "Las contraseñas no coinciden.");
                    return "Edit_user-profile";  // Retorna al formulario con mensaje de error
                }
            }
            userRepository.save(user);
        return "redirect:/profile";
        }else{
            return "404";
        }
    }
}
    
    
    
    


