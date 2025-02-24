package es.codeurjc.backend.Controllers;


import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;


import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Model.User;
import es.codeurjc.backend.Service.ActivityService;
import es.codeurjc.backend.Service.UserService;

import es.codeurjc.backend.Repository.ActivityRepository;
import java.util.Base64;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;



@Controller
public class activityController {

    @Autowired
    private ActivityService activityService;

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
            String formattedDate = sdf.format(activity.getCreationDate().getTime());
            activity.setFormattedCreationDate(formattedDate);
        }

        model.addAttribute("allActivities", activities);
        model.addAttribute("activityCount", activityService.activityCount());
        model.addAttribute("userCount", userService.countUsers());
        return "admin_activities";
    }
    
    @GetMapping("/removeActivity/{id}")
    @Transactional
    public String removeActivity(@PathVariable long id,Model model) {
        Optional<Activity> activity = activityService.findById(id);
        if(activity.isPresent()){
            List <User> users = activity.get().getUsers();
            for(User user: users){
                user.getActivities().remove(activity.get());
                userService.save(user);
            }
            activityService.delete(id);
            model.addAttribute("activity", activity.get());
            return "redirect:/admin_activities";
        }else{
            return "404";
        }
        
    }
    @GetMapping("/error")
    public String showError() {
        return "404";
    }
    
    
}

