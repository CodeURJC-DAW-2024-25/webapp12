package es.codeurjc.backend.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Service.ActivityService;

@Controller
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/")
    public String showActivities(Model model) {
        List<Activity> activities = activityService.findAll();
        model.addAttribute("activities", activities);
        return "index"; // Nombre del HTML en templates/
    }
}

