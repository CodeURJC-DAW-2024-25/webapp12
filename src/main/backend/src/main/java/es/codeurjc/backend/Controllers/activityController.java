package es.codeurjc.backend.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Service.ActivityService;
import es.codeurjc.backend.Service.UserService;




@Controller
public class activityController {

    @Autowired
    private ActivityService activityService;

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
        model.addAttribute("allActivities", activityService.findAll());
        model.addAttribute("activityCount", activityService.activityCount());
        model.addAttribute("userCount", userService.countUsers());
        return "admin_activities";
    }
    
}

