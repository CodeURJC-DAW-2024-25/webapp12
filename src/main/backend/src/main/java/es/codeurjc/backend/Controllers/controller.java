package es.codeurjc.backend.Controllers;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Service.ActivityService;

@Controller
public class controller {
    
    @Autowired
    private ActivityService activityService;


    @GetMapping("/index")
    public String home(Model model) {
        System.out.println("Llegó la petición a /index");  // Confirmar si el controlador está siendo llamado
        List<Activity> activities = activityService.getAllActivities();
        System.out.println("Actividades cargadas: " + activities);  // Ver las actividades cargadas
        model.addAttribute("activities", activities);
        return "index";
    }



    
}
