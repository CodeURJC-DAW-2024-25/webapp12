package es.codeurjc.backend.Controllers;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;


import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Model.User;
import es.codeurjc.backend.Service.ActivityService;
import es.codeurjc.backend.Service.UserService;
import jakarta.transaction.Transactional;
import es.codeurjc.backend.Repository.ActivityRepository;







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
        model.addAttribute("activities", activities);
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

    
    
    
    
}

