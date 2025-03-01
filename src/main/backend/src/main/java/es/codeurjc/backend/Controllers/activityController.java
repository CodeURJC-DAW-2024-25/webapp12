package es.codeurjc.backend.Controllers;


import java.io.IOException;
import java.security.Principal;
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
import es.codeurjc.backend.Model.Place;
import es.codeurjc.backend.Model.Review;
import es.codeurjc.backend.Model.User;
import es.codeurjc.backend.Service.ActivityService;
import es.codeurjc.backend.Service.PlaceService;
import es.codeurjc.backend.Service.ReviewService;
import es.codeurjc.backend.Service.UserService;

import es.codeurjc.backend.Repository.ActivityRepository;
import es.codeurjc.backend.Repository.PlaceRepository;
import es.codeurjc.backend.Repository.UserRepository;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;


import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;




import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;




@Controller
public class activityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlaceService placeService;

    @GetMapping("/")
    public String showActivities(Model model) {
        int page = 0;
        Page<Activity> activities = activityService.getActivitiesPaginated(page);
        //List<Activity> activities = activityService.findAll();

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

    @GetMapping("/moreActivities") 
    public String LoadMoreActivities(@RequestParam int page, Model model) { 
        Page<Activity> activities = activityService.getActivitiesPaginated(page);
        model.addAttribute("activities", activities.getContent());
        return "moreActivities";
    }



    @GetMapping("/admin_activities")
    public String showAdminActivities(Model model,HttpServletRequest request,Principal principal) {
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("user", request.isUserInRole("USER"));

        String userEmail = principal.getName();
        User user  = userRepository.findByEmail(userEmail); 
   
        if (user.getImageFile() != null) {
            try {
                byte[] imageBytes = user.getImageFile().getBytes(1, (int) user.getImageFile().length());
                String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                user.setImageString("data:image/png;base64," + imageBase64);
            } catch (SQLException e) {
                e.printStackTrace();
                user.setImageString("nofoto.png"); // Imagen por defecto
            }
        } else {
            user.setImageString("nofoto.png");
        }

        List<Activity> activities = activityRepository.findAll();
        List<Activity> subscribedActivities = activityService.findEventsSubscribe(user);

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
        model.addAttribute("countActivitiesSubscribed", subscribedActivities.size());
        model.addAttribute("activityCount", activityService.activityCount());
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("userRegister", user);
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
            return "404";  // Página de error
        }
    
        Activity activity = optionalActivity.get();
    
        
        // Obtener todas las reviews de la actividad
        List<Review> reviews = reviewService.findByActivity_Id(id);
        model.addAttribute("reviews", reviews);

        // Obtener el lugar asociado a la actividad
        Place place = activity.getPlace(); 
        model.addAttribute("place", place);






        System.out.println("Nombre de actividad: " + activity.getName());
        System.out.println("Descripción: " + activity.getDescription());
        System.out.println("Categoría: " + activity.getCategory());
        System.out.println("Vacantes: " + activity.getVacancy());
        System.out.println("Imagen: " + activity.getImageString());
        System.out.println("Lugar: " + (place != null ? place.getName() : "No tiene lugar asignado"));
        System.out.println("Número de comentarios: " + reviews.size());
        

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
    public String showFormNewActivity(Model model) {

        model.addAttribute("allPlaces", placeService.getAllPlaces());
        return "create_activity";
    }

    @PostMapping("/createActivity")
    public String createNewActivity(@ModelAttribute Activity activity, @RequestParam("placeId") Long placeId,@RequestParam("file") MultipartFile imagFile) {
       try{
            if(!imagFile.isEmpty()){
                activity.setImageFile(BlobProxy.generateProxy(imagFile.getInputStream(), imagFile.getSize()));
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date()); // Convierte Date a Calendar
            activity.setCreationDate(calendar);
            Optional<Place> optionalPlace = placeRepository.findById(placeId);
            if (optionalPlace.isPresent()) {
                Place place = optionalPlace.get();
                activity.setPlace(place);
            }else{
                return "404";
            }
            activityService.saveActivity(activity);
            return "redirect:/admin_activities";
       }catch(IOException e){
            e.printStackTrace();
            return "404";
       }
    }
    
    @GetMapping("/editActivity/{id}")
    public String showEditActivityForm(@PathVariable Long id, Model model) {
        // Buscar la actividad por su ID
        Optional<Activity> optionalActivity = activityService.findById(id);
        Activity activity = optionalActivity.get();

        if (optionalActivity.isEmpty()) {
            model.addAttribute("errorMessage", "Actividad no encontrada.");
            return "404";  // Página de error si no se encuentra la actividad
        }
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

        Place place = activity.getPlace();
        model.addAttribute("activity", activity);
        model.addAttribute("place", place);
        model.addAttribute("places", placeService.getAllPlaces());

    // Retorna la plantilla de edición
        return "Edit_activity";  // Nombre del archivo HTML para editar
    }

    @PostMapping("/editActivity/{id}")
    public String editActivity(@PathVariable Long id, @ModelAttribute Activity updatedActivity, @RequestParam("file") MultipartFile imagFile) throws IOException {
        Optional<Activity> optionalActivity = activityService.findById(id);
        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();

            // Actualiza los campos de la actividad con los valores del formulario
            activity.setName(updatedActivity.getName());
            activity.setDescription(updatedActivity.getDescription());
            activity.setCategory(updatedActivity.getCategory());
            activity.setVacancy(updatedActivity.getVacancy());
            activity.setPlace(updatedActivity.getPlace());

            // Si se sube una nueva imagen, actualiza la imagen
            if (!imagFile.isEmpty()) {
                activity.setImageFile(BlobProxy.generateProxy(imagFile.getInputStream(), imagFile.getSize()));
            }

            // Guarda los cambios
            activityService.saveActivity(activity);

            return "redirect:/admin_activities";  // Redirige a la vista de administración de actividades
        }

        return "404";  // Si no se encuentra la actividad, muestra un error
    }



    
    
    
}

