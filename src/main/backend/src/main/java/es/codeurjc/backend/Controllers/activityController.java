package es.codeurjc.backend.Controllers;


import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;


import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Model.Review;
import es.codeurjc.backend.Model.User;
import es.codeurjc.backend.Service.ActivityService;
import es.codeurjc.backend.Service.ReviewService;
import es.codeurjc.backend.Service.UserService;

import es.codeurjc.backend.Repository.ActivityRepository;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;



@Controller
public class activityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String showActivities(Model model) {
        List<Activity> activities = activityService.findAll();

        // Convertir la imagen Blob en base64 y agregarla al modelo
        for (Activity activity : activities) {
            if (activity.getImageFile() != null) {
                try {
                    byte[] imageBytes = activity.getImageFile().getBytes(1, (int) activity.getImageFile().length());
                    String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                    activity.setImageString(imageBase64); 
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                // Asignar directamente la ruta de la imagen predeterminada
                activity.setImageString("nofoto.png");
                
            }
        }

        model.addAttribute("activities", activities);





        // Imprimir el nombre del usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            System.out.println("Usuario autenticado: " + username);
        }


        return "index"; 
    }



    @GetMapping("/admin_activities")
    public String showAdminActivities(Model model) {
        List<Activity> activities = activityRepository.findAll();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for(Activity activity: activities){
            if (activity.getImageFile() != null) {
                try {
                    byte[] imageBytes = activity.getImageFile().getBytes(1, (int) activity.getImageFile().length());
                    String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                    activity.setImageString(imageBase64); 
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                activity.setImageString("/activities/" + activity.getId() + "/image");
            
            }
            String formattedDate = sdf.format(activity.getCreationDate().getTime());
            activity.setFormattedCreationDate(formattedDate);
        }

        model.addAttribute("allActivities", activities);
        model.addAttribute("activityCount", activityService.activityCount());
        model.addAttribute("userCount", userService.countUsers());
        return "admin_activities";
    }
    @GetMapping("/404")
    public String showError() {
        return "404";
    }
    @Transactional
    @GetMapping("/removeActivity/{id}")
    public String removeActivity(@PathVariable long id,Model model) {
        Optional<Activity> optionalActivity = activityService.findById(id);
        if(optionalActivity.isPresent()){
            Activity activity = optionalActivity.get();
            List <User> users = activity.getUsers();
            for(User user: users){
                user.getActivities().remove(activity);
                userService.save(user);
            }
            activityService.delete(id);
            return "redirect:/admin_activities";
        }else{
            return "404";
        }
        
    }



    @GetMapping("/activity/{id}")
    public String getActivityDetail(@PathVariable Long id, Model model) {
        Optional<Activity> optionalActivity = activityService.findById(id);
    
        if (optionalActivity.isEmpty()) {
            model.addAttribute("errorMessage", "Actividad no encontrada.");
            return "error";  // Página de error
        }
    
        Activity activity = optionalActivity.get();
        
        // Obtener todas las reviews de la actividad
        List<Review> reviews = reviewService.findByActivity_Id(id);
        model.addAttribute("reviews", reviews);
    
        // Convertir la imagen Blob a Base64 para Mustache
        if (activity.getImageFile() != null) {
            try {
                byte[] imageBytes = activity.getImageFile().getBytes(1, (int) activity.getImageFile().length());
                String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                activity.setImageString("data:image/png;base64," + imageBase64);
            } catch (SQLException e) {
                e.printStackTrace();
                activity.setImageString("nofoto.png"); // Imagen por defecto
            }
        } else {
            activity.setImageString("nofoto.png");
        }
    
        model.addAttribute("activity", activity);
        model.addAttribute("reviews", reviews); // Agregar las reviews al modelo
    
        return "activity";  // Nombre del archivo .mustache
    }
    
    /*@PostMapping("/activity/{id}/addReview")
        public String addReview(
            @PathVariable Long id,
            @RequestParam int starsValue,
            @RequestParam String description) {

            Optional<Activity> optionalActivity = activityService.findById(id);
            User user = userService.getCurrentUser(); // Suponiendo que tienes un método para obtener el usuario autenticado

            if (optionalActivity.isPresent() && user != null) {
                Activity activity = optionalActivity.get();
                Review review = new Review(starsValue, description, activity, user);
                reviewService.save(review);
            }

            return "redirect:/activity/" + id; // Redirige a la misma página para ver la nueva reseña
        }*/


    @GetMapping("/createActivity")
    public String showFormNewActivity() {
        return "create_activity";
    }

    @PostMapping("/createActivity")
    public String createNewActivity(@ModelAttribute Activity activity,@RequestParam("file") MultipartFile imagFile) {
       try{
            if(!imagFile.isEmpty()){
                activity.setImageFile(BlobProxy.generateProxy(imagFile.getInputStream(), imagFile.getSize()));
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date()); // Convierte Date a Calendar
            activity.setCreationDate(calendar);
            activityService.saveActivity(activity);
            return "redirect:/admin_activities";
       }catch(IOException e){
            e.printStackTrace();
            return "404";
       }
    }
    
    
    
}

