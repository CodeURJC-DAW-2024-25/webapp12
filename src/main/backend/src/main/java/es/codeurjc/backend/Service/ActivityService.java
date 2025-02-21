package es.codeurjc.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Model.User;
import es.codeurjc.backend.Repository.ActivityRepository;

import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository; 

    public List<Activity> getAllActivities() {
        List<Activity> activities = activityRepository.findAll();
        System.out.println("Actividades desde el repositorio: " + activities);  // Esto te mostrará las actividades por consola
        return activities;
    }
    
    public Activity saveActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public List<Activity> findAll() {
       return activityRepository.findAll();
    }

    public long activityCount(){
        return activityRepository.count();
    }

    public Optional <Activity>  findById(long id){
        return activityRepository.findById(id);
    } 
    
    public void delete(long id){
        activityRepository.deleteById(id);
    }

    public List<Activity> findEventsSubscribe(User user){
        return activityRepository.findByUserSubscribe(user);
    }
}

