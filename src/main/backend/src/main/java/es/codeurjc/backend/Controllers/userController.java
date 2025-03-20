package es.codeurjc.backend.controllers;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.codeurjc.backend.dto.UserDto;
import es.codeurjc.backend.model.Activity;
import es.codeurjc.backend.model.Review;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.service.ActivityService;
import es.codeurjc.backend.service.EmailService;
import es.codeurjc.backend.service.PlaceService;
import es.codeurjc.backend.service.ReviewService;
import es.codeurjc.backend.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;





@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ActivityService activityService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PlaceService placeService;

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/loginError")
    public String showErrorLogin() {
        return "error";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }



    @PostMapping("/signup")
    public String signup(User user, RedirectAttributes attributes) throws MessagingException {
        if (userService.existsByEmail(user.getEmail())) {
            return "redirect:/register";
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(List.of("USER"));
            userService.save(user);
    
            
            String htmlMessage = "<div style='font-family: Arial, sans-serif; padding: 15px; border: 2px solid #ddd; border-radius: 10px; background-color: #f9f9f9;'>"
                    + "<h2 style='color: #2d89ef;'>👋 ¡Bienvenido a nuestra aplicación, " + user.getName() + "! 🎉</h2>"
                    + "<p style='font-size: 16px; color: #333;'>Gracias por registrarte en nuestra plataforma. Estamos encantados de tenerte aquí. 😊</p>"
                    + "<p style='font-size: 16px; color: #333;'> Te damos la vienvenida a nuestro FABULOSO resort.</p>"
                    + "<p style='font-size: 16px; color: #333;'>🚀 ¡Esperamos que disfrutes de la experiencia!</p>"
                    + "<p style='font-size: 14px; color: #777;'>Saludos,<br><strong>El equipo de Pixel Paradise</strong></p>"
                    + "</div>";
    
           
            emailService.sendEmail(
                user.getEmail(),
                "¡Bienvenido a nuestra aplicación!",
                htmlMessage
            );
    
            return "redirect:/login";
        }
    }
    

    
    @GetMapping("/adminUsers")
    public String showAdminUsers(Model model,HttpServletRequest request,Principal principal) {


        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("user", request.isUserInRole("USER"));

        String userEmail = principal.getName();
        User user  = userService.findByEmail(userEmail); 
        
        List<Activity> subscribedActivities = activityService.findEventsSubscribe(user);
        int page = 0;
        Pageable pageable = PageRequest.of(page, 10);
        Page<UserDto> users = userService.getAllUsersPaginated(pageable);
        System.out.println("Has more users: " + users.hasNext());
        model.addAttribute("users", users.getContent()); 
        model.addAttribute("hasMore", users.hasNext());
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("countActivitiesSubscribed", subscribedActivities.size());
        model.addAttribute("activityCount", activityService.activityCount());
        model.addAttribute("userRegister", user);
        return "adminUsers";
    }

    @GetMapping("/moreUsers") 
    public String LoadMoreUser(@RequestParam int page, Model model) { 
        System.out.println("Cargando usuarios, página: " + page);
    
        try {
            
            int totalPages = userService.getUsersPaginated(0).getTotalPages();
    
           
            if (page >= totalPages) {
                model.addAttribute("users", new ArrayList<>()); 
                model.addAttribute("hasMore", false);
                return "moreUsers";
            }
    
            Page<User> users = userService.getUsersPaginated(page);
    
            if (users == null) {
                throw new RuntimeException("userService.findAll(pageable) retornó null");
            }
    
            model.addAttribute("users", users.getContent()); 
            boolean hasMore = page < users.getTotalPages() - 1;
            model.addAttribute("hasMore", hasMore);
    
            System.out.println("Usuarios cargados: " + users.getContent().size());
            System.out.println("Total páginas: " + users.getTotalPages());
            System.out.println("Has more users: " + hasMore);
    
            return "moreUsers";
        } catch (Exception e) {
            System.err.println("Error en /moreUsers: " + e.getMessage());
            e.printStackTrace();
            return "errorPage";  
        }
    }
    
        
    
    

    @GetMapping("/user/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

		Optional<User> optionalUser = userService.findById(id);
		if (optionalUser.isPresent() && optionalUser.get().getImageFile() != null) {

			Resource file = new InputStreamResource(optionalUser.get().getImageFile().getBinaryStream());

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
					.contentLength(optionalUser.get().getImageFile().length()).body(file);

		} else {
			return ResponseEntity.notFound().build();
		}
	}

    private void updateImage(User user, boolean removeImage, MultipartFile imageField) throws IOException, SQLException {
		
		if (!imageField.isEmpty()) {
			user.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
			user.setImage(true);
		} else {
			if (removeImage) {
				user.setImageFile(null);
				user.setImage(false);
            } else if (imageField != null && !imageField.isEmpty()) {
                user.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
                user.setImage(true);
            }
		}
	}



   @GetMapping("/profile")
    public String showProfile(Model model, Principal principal, HttpServletRequest request) {
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("user", request.isUserInRole("USER"));
        String userEmail = principal.getName();
        User user = userService.findByEmail(userEmail);

        if (user != null) {
            model.addAttribute("userRegister", user);
            model.addAttribute("userCount", userService.countUsers());
            model.addAttribute("activityCount", activityService.activityCount());
            model.addAttribute("id", user.getId());

            
            int page = 0;
            Page<Activity> subscribedActivities = activityService.getActivitiesSubscribedPaginated(page, user);
            model.addAttribute("subscribedActivities", subscribedActivities);
            model.addAttribute("countActivitiesSubscribed", subscribedActivities.getTotalElements());
            return "profile";
        } else {
            return "redirect:/error";
        }
    }

    @GetMapping("/moreActivitiesSubscribed") 
    public String LoadMoreActivities(@RequestParam int page, Model model, Principal principal) { 
        String userEmail = principal.getName();
        User user = userService.findByEmail(userEmail);
        Page<Activity> activities = activityService.getActivitiesSubscribedPaginated(page, user);
        model.addAttribute("subscribedActivities", activities.getContent());
        boolean hasMore = page < activities.getTotalPages() - 1;
        model.addAttribute("hasMore", hasMore);
        model.addAttribute("subscribedActivities", activities.getContent());
        return "moreActivitiesSubscribed";
    } 





    @Transactional
    @PostMapping("/removeUser")
    public String removeUser(@RequestParam Long id) {
        Optional<User> optionalUser = userService.findById(id);
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

            userService.delete(user);
            return "redirect:/adminUsers";
        }else{
            return "error";
        }
        
    }

    @GetMapping("/editUserProfile/{id}")
    public String showEditProfile(Principal principal,Model model,HttpServletRequest request, @PathVariable long id) {
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("user", request.isUserInRole("USER"));
        Optional<User> optionalUser = userService.findById(id);
        
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            List<Activity> subscribedActivities = activityService.findEventsSubscribe(user);
            model.addAttribute("userRegistered", user);
            model.addAttribute("countActivitiesSubscribed", subscribedActivities.size());
            model.addAttribute("userCount", userService.countUsers());
            model.addAttribute("activityCount", activityService.activityCount());
            return "editUserProfile";
        }else{
            return "error";
        }
    }   
    

    @PostMapping("/editUserProfile")
    public String updateProfile(Model model, @ModelAttribute User userUpdated,
    @RequestParam("file") MultipartFile imagFile,
    @RequestParam(value = "removeImage", required = false, defaultValue = "false") boolean removeImage)
    throws IOException, SQLException{
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail);
        try{
            if(user != null){
                user.setName(userUpdated.getName());
                user.setSurname(userUpdated.getSurname());
                user.setDni(userUpdated.getDni());
                user.setPhone(userUpdated.getPhone());
                updateImage(user, removeImage, imagFile);
                userService.save(user);
                return "redirect:/profile";
            }
            else{
                return "error";
            }
        }catch(IOException e){
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/statistics")
    public String showStatistics(HttpServletRequest request,Model model,Principal principal) throws JsonProcessingException {
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("user", request.isUserInRole("USER"));

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail);

        if(user != null){
            List<Activity> subscribedActivities = activityService.findEventsSubscribe(user);
            Map<Integer, Long> activitiesByMonth = activityService.countActivitiesByMonth();
            
            List<Integer> activityData = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                activityData.add(activitiesByMonth.getOrDefault(i, 0L).intValue());
            }

            model.addAttribute("activityData", new ObjectMapper().writeValueAsString(activityData));
            model.addAttribute("reviewData", reviewService.countReviewsByValoration());
            model.addAttribute("userRegistered", user);
            model.addAttribute("subscribedActivities", subscribedActivities);
            model.addAttribute("countActivitiesSubscribed", subscribedActivities.size());
            model.addAttribute("userCount", userService.countUsers());
            model.addAttribute("activityCount", activityService.activityCount());
            model.addAttribute("placeCount", placeService.placeCount());

            return "statistics";
        }else{
            return "error";
        }
    }
    
}

    
    
    
    


