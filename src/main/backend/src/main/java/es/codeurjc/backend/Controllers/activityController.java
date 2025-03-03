package es.codeurjc.backend.Controllers;


import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
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

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;




@Controller
public class activityController {

    @Autowired
    private ActivityService activityService;

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
    public String showActivities(Model model, Principal principal) {
        int page = 0;
        Page<Activity> activities = activityService.getActivitiesPaginated(page);

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
        List<Place> places = placeRepository.findAll();
        model.addAttribute("activities", activities);
        model.addAttribute("allPlaces", places);
        
        // Obtener usuario autenticado a través de Principal
        if (principal != null) {
            String userEmail = principal.getName();
            System.out.println("Usuario autenticado: " + userEmail);

            // Buscar usuario en la base de datos por email
            User user = userRepository.findByEmail(userEmail);
            if (user != null) {
                // Obtener actividades recomendadas
                List<Activity> recommendedActivities = activityService.recommendActivities(user.getId());
                // Convertir la imagen Blob en base64 y agregarla al modelo
                for (Activity activity : recommendedActivities) {
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
                model.addAttribute("recommendedActivities", recommendedActivities);
            } else {
                System.out.println("No se encontró el usuario con email: " + userEmail);
       }
    }
        return "index"; 
    }

    @GetMapping("/moreActivities") 
    public String LoadMoreActivities(@RequestParam int page, Model model) { 
        Page<Activity> activities = activityService.getActivitiesPaginated(page);
        model.addAttribute("activities", activities.getContent());
        boolean hasMore = page < activities.getTotalPages() - 1;
        model.addAttribute("hasMore", hasMore);
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
    public String getActivityDetail(@PathVariable Long id, Model model, Principal principal) {
        Optional<Activity> optionalActivity = activityService.findById(id);
        
        if (optionalActivity.isEmpty()) {
            model.addAttribute("errorMessage", "Actividad no encontrada.");
            return "404";  // Página de error
        }
    
        Activity activity = optionalActivity.get();
        
        // Obtener todas las reviews de la actividad
        //List<Review> reviews = reviewService.findByActivity_Id(id);
        //model.addAttribute("reviews", reviews);

        int page = 0;
        Page<Review> reviews = reviewService.getReviewsPaginated(id, page);
        model.addAttribute("reviews", reviews);
    
        // Obtener el lugar asociado a la actividad
        Place place = activity.getPlace(); 
        model.addAttribute("place", place);
        
        // Obtener el usuario autenticado
        if (principal != null) {
            String userEmail = principal.getName();
            User user = userRepository.findByEmail(userEmail);
    
            // Verificar si el usuario está suscrito a esta actividad
            List<Activity> subscribedActivities = activityService.findEventsSubscribe(user);
            boolean isSubscribed = subscribedActivities.contains(activity);  // Verifica si la actividad está en la lista de actividades del usuario
            
            model.addAttribute("isSubscribed", isSubscribed);
        }
    
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
    
        return "activity";  // Nombre del archivo .mustache
    }

   /* @GetMapping("/moreReviews")
    public String loadMoreReviews(@RequestParam Long activityId, @RequestParam int page, Model model) {
        Page<Review> reviews = reviewService.getReviewsPaginated(activityId, page);
        
        
        boolean hasMore = page < reviews.getTotalPages() - 1;
        
        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("hasMore", hasMore);
        
        
        return "moreReviews";
    }*/

    @GetMapping("/moreReviews")
    public String loadMoreReviews(@RequestParam Long activityId, @RequestParam int page, Model model) {
        Page<Review> reviews = reviewService.getReviewsPaginated(activityId, page);
        
        // Buscar la actividad por ID
        Optional<Activity> optionalActivity = activityService.findById(activityId);
        if (optionalActivity.isEmpty()) {
            model.addAttribute("errorMessage", "Actividad no encontrada.");
            return "404";  // Página de error
        }

        Activity activity = optionalActivity.get();
        model.addAttribute("activity", activity);  // Agregamos activity al modelo
        
        // Asegurar si hay más páginas
        boolean hasMore = page < reviews.getTotalPages() - 1;
        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("hasMore", hasMore);

        return "moreReviews";  // Nombre de la plantilla Mustache
    }

    
    
    
    
    @GetMapping("/createActivity")
    public String showFormNewActivity(Model model) {

        model.addAttribute("allPlaces", placeService.getAllPlaces());
        return "create_activity";
    }

    @PostMapping("/createActivity")
    public String createNewActivity(@ModelAttribute Activity activity, @RequestParam("placeId") Long placeId, @RequestParam("file") MultipartFile imagFile) {
        try {
            // Si el archivo no está vacío, lo convertimos en un Blob y lo asignamos a la actividad
            if (!imagFile.isEmpty()) {
                activity.setImageFile(BlobProxy.generateProxy(imagFile.getInputStream(), imagFile.getSize()));
            }

            activity.setCreationDateMethod();

            // Obtener el lugar de la actividad
            Optional<Place> optionalPlace = placeRepository.findById(placeId);
            if (optionalPlace.isPresent()) {
                Place place = optionalPlace.get();
                activity.setPlace(place);
            } else {
                return "404"; // Si no se encuentra el lugar, retorna error
            }

            // Convertir la fecha de actividad desde el tipo java.util.Date a java.sql.Date
            java.util.Date utilDate = activity.getActivityDate(); // Suponiendo que el formulario pasa la fecha como java.util.Date
            if (utilDate != null) {
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                activity.setActivityDate(sqlDate); // Establecer la fecha de actividad convertida
            }

            // Guardar la actividad
            activityService.saveActivity(activity);
            return "redirect:/admin_activities"; // Redirigir a la página de administración de actividades
        } catch (IOException e) {
            e.printStackTrace();
            return "404"; // Si ocurre un error, se maneja el error y retorna una página de error
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

    @GetMapping("/search_page")
    public String showSearchs(Model model, Principal principal, @RequestParam("placeId") Long placeId) {
        //int page = 0;  // Si deseas implementar paginación, ajusta la variable `page`
       // Page<Activity> activities = activityService.getActivitiesPaginated(page);

        if (principal != null) {
            String userEmail = principal.getName();
            System.out.println("Usuario autenticado: " + userEmail);

            User user = userRepository.findByEmail(userEmail);
            if (user != null) {
                List<Activity> recommendedActivities = activityService.recommendActivities(user.getId());
                for (Activity activity : recommendedActivities) {
                    if (activity.getImageFile() != null) {
                        try {
                            byte[] imageBytes = activity.getImageFile().getBytes(1, (int) activity.getImageFile().length());
                            String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                            activity.setImageString(imageBase64);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        activity.setImageString("nofoto.png");
                    }
                }
            }
        }
        // Buscar actividades por lugar
        Optional<Place> optionalPlace = placeRepository.findById(placeId);

        if (optionalPlace.isPresent()) {
            Place place = optionalPlace.get();
            List<Activity> activitiesByPlace = activityRepository.findByPlace(place);
             // Convertir la imagen Blob en base64 y agregarla al modelo
            for (Activity activity : activitiesByPlace) {
                if (activity.getImageFile() != null) {
                    try {
                        byte[] imageBytes = activity.getImageFile().getBytes(1, (int) activity.getImageFile().length());
                        String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                        activity.setImageString(imageBase64);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    activity.setImageString("nofoto.png");
                }
            }
            model.addAttribute("activitiesByPlace", activitiesByPlace); // Pasa las actividades al modelo
        } else {
            return "404";  // Si no se encuentra el lugar, redirige a una página de error
        }
        return "search_page"; // Devuelve la vista "search_page" con las actividades
    }

    @PostMapping("/activity/{id}/reserve")
    public String reserveActivity(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // Si el usuario no está autenticado, redirigir al login
        }

        String userEmail = principal.getName();
        User user = userRepository.findByEmail(userEmail);

        if (user == null) {
            return "404"; // Si el usuario no existe, devolver error
        }

        boolean success = activityService.reserveActivity(id, user.getId());

        if (!success) {
            return "redirect:/activity/" + id + "?error=cupo_lleno";
        }

        return "redirect:/activity/" + id + "?success=reserva_exitosa";
    }

    
}

