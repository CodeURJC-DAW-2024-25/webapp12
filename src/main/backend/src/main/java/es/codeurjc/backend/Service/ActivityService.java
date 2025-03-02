package es.codeurjc.backend.Service;

//import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Model.Place;
import es.codeurjc.backend.Model.User;
import es.codeurjc.backend.Repository.ActivityRepository;
import es.codeurjc.backend.Repository.UserRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.stream.Collectors;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository; 
    @Autowired
    private UserRepository userRepository;

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

    public Page<Activity> getActivitiesPaginated(int page) {
        int size = 4;
        Pageable pageable = PageRequest.of(page, size);
        return activityRepository.findAll(pageable);
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
        return activityRepository.findByUsers(user);
   }
    public Activity getActivityById(Long id) {
        // Utiliza el repositorio para buscar la actividad por su ID
        Optional<Activity> activityOptional = activityRepository.findById(id);
        //si no esta devuelve null
        return activityOptional.orElse(null);
       
    }
// ALGORITMO DE RECOMENDACIÓN:
public List<Activity> recommendActivities(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
    // Cargar las actividades del usuario para evitar problemas de inicialización perezosa
    List<Activity> userActivities = new ArrayList<>(user.getActivities());
    
    Set<String> categories = userActivities.stream()
            .map(Activity::getCategory)
            .collect(Collectors.toSet());
    
    Set<Place> places = userActivities.stream()
            .map(Activity::getPlace)
            .collect(Collectors.toSet());
    
    return activityRepository.findSimilarActivities(categories, places, userActivities);
}
    
public Map<Integer, Long> countActivitiesByMonth() {
        List<Activity> activities = activityRepository.findAll();
        Map<Integer, Long> activitiesByMonth = new HashMap<>();

        // Inicializar el mapa con 0 actividades en cada mes
        for (int i = 1; i <= 12; i++) {
            activitiesByMonth.put(i, 0L);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (Activity activity : activities) {
            String formattedDate = activity.getFormattedCreationDate();
            if (formattedDate != null && !formattedDate.isEmpty()) {
                try {
                    Date date = dateFormat.parse(formattedDate);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int month = calendar.get(Calendar.MONTH) + 1; // +1 porque Calendar empieza en 0

                    activitiesByMonth.put(month, activitiesByMonth.get(month) + 1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return activitiesByMonth;
    }
}




