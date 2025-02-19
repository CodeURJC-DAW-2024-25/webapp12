package es.codeurjc.backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.backend.Service.ActivityService;

@Controller
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/activities")
    public String showActivities(Model model) {
        // Obtener todas las actividades del servicio
        model.addAttribute("activities", activityService.getAllActivities());

        // Retornar la vista
        return "index";  // Asegúrate de que el nombre de la vista sea el correcto
    }
}

