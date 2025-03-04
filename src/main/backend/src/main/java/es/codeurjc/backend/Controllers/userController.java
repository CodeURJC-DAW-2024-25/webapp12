package es.codeurjc.backend.Controllers;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.text.SimpleDateFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.codeurjc.backend.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import es.codeurjc.backend.Service.ActivityService;
import es.codeurjc.backend.Service.PlaceService;
import es.codeurjc.backend.Service.ReviewService;


import org.springframework.ui.Model;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Model.Review;
import es.codeurjc.backend.Model.User;
import es.codeurjc.backend.Repository.UserRepository;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        
        
        List<Activity> subscribedActivities = activityService.findEventsSubscribe(user);
        


        
        int page = 0;
        Page<User> users = userService.getUsersPaginated(page);
        System.out.println("Has more users: " + users.hasNext());


    
        model.addAttribute("users", users.getContent()); // Asegurar que es "users"

        model.addAttribute("hasMore", users.hasNext());
        
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("countActivitiesSubscribed", subscribedActivities.size());
        model.addAttribute("activityCount", activityService.activityCount());
        model.addAttribute("userRegister", user);
        return "admin_users";
    }

    @GetMapping("/moreUsers") 
    public String LoadMoreUser(@RequestParam int page, Model model) { 
        System.out.println("Cargando usuarios, página: " + page);
    
        try {
            // Obtener el total de páginas disponibles
            int totalPages = userService.getUsersPaginated(0).getTotalPages();
    
            // Si la página solicitada es mayor o igual al total, no hay más usuarios
            if (page >= totalPages) {
                model.addAttribute("users", new ArrayList<>()); // No hay más usuarios
                model.addAttribute("hasMore", false);
                return "moreUsers";
            }
    
            Page<User> users = userService.getUsersPaginated(page);
    
            if (users == null) {
                throw new RuntimeException("userRepository.findAll(pageable) retornó null");
            }
    
            model.addAttribute("users", users.getContent()); // Asegurar que es "users"
            boolean hasMore = page < users.getTotalPages() - 1;
            model.addAttribute("hasMore", hasMore);
    
            System.out.println("Usuarios cargados: " + users.getContent().size());
            System.out.println("Total páginas: " + users.getTotalPages());
            System.out.println("Has more users: " + hasMore);
    
            return "moreUsers";
        } catch (Exception e) {
            System.err.println("Error en /moreUsers: " + e.getMessage());
            e.printStackTrace();
            return "errorPage";  // Página de error si hay un problema
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
        User user = userRepository.findByEmail(userEmail);

        if (user != null) {
            List<Activity> subscribedActivities = activityService.findEventsSubscribe(user);

            // Formatear fechas antes de enviarlas a la vista
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            List<Map<String, Object>> formattedActivities = new ArrayList<>();

            for (Activity activity : subscribedActivities) {
                Map<String, Object> activityMap = new HashMap<>();
                activityMap.put("id", activity.getId());
                activityMap.put("name", activity.getName());
                activityMap.put("vacancy", activity.getVacancy());
                activityMap.put("category", activity.getCategory());
                activityMap.put("activityDate", dateFormat.format(activity.getActivityDate())); // Formatear fecha

                formattedActivities.add(activityMap);
            }

            model.addAttribute("userRegister", user);
            model.addAttribute("subscribedActivities", formattedActivities);
            model.addAttribute("countActivitiesSubscribed", subscribedActivities.size());
            model.addAttribute("userCount", userService.countUsers());
            model.addAttribute("activityCount", activityService.activityCount());
            model.addAttribute("id", user.getId());
            return "profile";
        } else {
            return "redirect:/404";
        }
    }
    @Transactional
    @PostMapping("/removeUser")
    public String removeUser(@RequestParam Long id) {
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

    @GetMapping("/Edit_user-profile/{id}")
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
            return "Edit_user-profile";
        }else{
            return "404";
        }
    }   
    

    @PostMapping("/Edit_user-profile")
    public String updateProfile(Model model, @ModelAttribute User userUpdated,
    @RequestParam("file") MultipartFile imagFile,
    @RequestParam(value = "removeImage", required = false, defaultValue = "false") boolean removeImage)
    throws IOException, SQLException{
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail);
        try{
            if(user != null){
                user.setName(userUpdated.getName());
                user.setSurname(userUpdated.getSurname());
                user.setDni(userUpdated.getDni());
                user.setPhone(userUpdated.getPhone());
                updateImage(user, removeImage, imagFile);
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
    public String showStatistics(HttpServletRequest request,Model model,Principal principal) throws JsonProcessingException {
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("user", request.isUserInRole("USER"));

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail);

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
            return "404";
        }
    }
    
}

    
    
    
    


