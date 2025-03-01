package es.codeurjc.backend.Controllers;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Base64;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;




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



    @PostMapping("/signup")
    public String signup(User user, RedirectAttributes attributes) {
        if (userService.existsByEmail(user.getEmail())) {
            return "redirect:/register";
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(List.of("USER"));
            userService.save(user);
            return "redirect:/login";
        }
    }

    
    @GetMapping("/admin_users")
    public String showAdminUsers(Model model,HttpServletRequest request,Principal principal) {
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("user", request.isUserInRole("USER"));

        String userEmail = principal.getName();
        User user  = userRepository.findByEmail(userEmail); 
        if (user.getImageFile() != null) {
            try {
                byte[] imageBytes = user.getImageFile().getBytes(1, (int) user.getImageFile().length());
                String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                user.setImageString("data:image/png;base64," + imageBase64);
            } catch (SQLException e) {
                e.printStackTrace();
                user.setImageString("nofoto.png"); // Imagen por defecto
            }
        } else {
            user.setImageString("nofoto.png");
        }
        List <User> users = userRepository.findAll();
        for(User userList : users){
            if (userList.getImageFile() != null) {
                try {
                    byte[] imageBytes = userList.getImageFile().getBytes(1, (int) userList.getImageFile().length());
                    String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                    userList.setImageString("data:image/png;base64," + imageBase64);
                } catch (SQLException e) {
                    e.printStackTrace();
                    userList.setImageString("nofoto.png"); // Imagen por defecto
                }
            } else {
                userList.setImageString("nofoto.png");
            }
        }
        
        List<Activity> subscribedActivities = activityService.findEventsSubscribe(user);
        model.addAttribute("allUsers", users);
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("countActivitiesSubscribed", subscribedActivities.size());
        model.addAttribute("activityCount", activityService.activityCount());
        model.addAttribute("userRegister", user);
        return "admin_users";
    }


    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal,HttpServletRequest request) {
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("user", request.isUserInRole("USER"));
        String userEmail = principal.getName();
        User user  = userRepository.findByEmail(userEmail); 
        

        if(user != null){
            if (user.getImageFile() != null) {
                try {
                    byte[] imageBytes = user.getImageFile().getBytes(1, (int) user.getImageFile().length());
                    String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                    user.setImageString("data:image/png;base64," + imageBase64);
                } catch (SQLException e) {
                    e.printStackTrace();
                    user.setImageString("nofoto.png"); // Imagen por defecto
                }
            } else {
                user.setImageString("nofoto.png");
            }
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
            if (user.getImageFile() != null) {
                try {
                    byte[] imageBytes = user.getImageFile().getBytes(1, (int) user.getImageFile().length());
                    String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                    user.setImageString("data:image/png;base64," + imageBase64);
                } catch (SQLException e) {
                    e.printStackTrace();
                    user.setImageString("nofoto.png"); // Imagen por defecto
                }
            } else {
                user.setImageString("nofoto.png");
            }
            System.out.println(user.getDni());
            model.addAttribute("userRegistered", user);
            return "Edit_user-profile";
        }else{
            return "404";
        }
    }   
    

    @PostMapping("/Edit_user-profile")
    public String updateProfile(Model model, @ModelAttribute User userUpdated,@RequestParam("file") MultipartFile imagFile){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail);
        try{
            if(user!=null){
                user.setName(userUpdated.getName());
                user.setSurname(userUpdated.getSurname());
                user.setDni(userUpdated.getDni());
                user.setEmail(userUpdated.getEmail());
                user.setPhone(userUpdated.getPhone());
                if(!imagFile.isEmpty()){
                    user.setImageFile(BlobProxy.generateProxy(imagFile.getInputStream(), imagFile.getSize()));
                }
                userRepository.save(user);
                return "redirect:/profile";
            }
            else{
                return "404";
            }
        }catch(IOException e){
            e.printStackTrace();
            return "404";
        }
    }

    @GetMapping("/statistics")
    public String showStatistics(HttpServletRequest request,Model model,Principal principal) {
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("user", request.isUserInRole("USER"));

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail);

        if(user != null){
            if (user.getImageFile() != null) {
                try {
                    byte[] imageBytes = user.getImageFile().getBytes(1, (int) user.getImageFile().length());
                    String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                    user.setImageString("data:image/png;base64," + imageBase64);
                } catch (SQLException e) {
                    e.printStackTrace();
                    user.setImageString("nofoto.png"); // Imagen por defecto
                }
            } else {
                user.setImageString("nofoto.png");
            }
            List<Activity> subscribedActivities = activityService.findEventsSubscribe(user);
            model.addAttribute("userRegistered", user);
            model.addAttribute("subscribedActivities", subscribedActivities);
            model.addAttribute("countActivitiesSubscribed", subscribedActivities.size());
            model.addAttribute("userCount", userService.countUsers());
            model.addAttribute("activityCount", activityService.activityCount());
            

            return "statistics";
        }else{
            return "404";
        }
    }
    
}

    
    
    
    


