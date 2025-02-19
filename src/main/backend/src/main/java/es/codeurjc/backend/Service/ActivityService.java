package es.codeurjc.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Repository.ActivityRepository;

import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository; // Asegúrate de tener este repositorio configurado

    public List<Activity> getAllActivities() {
        List<Activity> activities = activityRepository.findAll();
        System.out.println("Actividades desde el repositorio: " + activities);  // Esto te mostrará las actividades por consola
        return activities;
    }
    
    public Activity saveActivity(Activity activity) {
        return activityRepository.save(activity);
    }
    
    
}

