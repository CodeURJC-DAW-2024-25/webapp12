package es.codeurjc.backend.Controllers;


import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import es.codeurjc.backend.Repository.ActivityRepository;
import es.codeurjc.backend.Repository.PlaceRepository;
import es.codeurjc.backend.Repository.UserRepository;



import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;




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

        
        List<Activity> subscribedActivities = activityService.findEventsSubscribe(user);

        
        model.addAttribute("countActivitiesSubscribed", subscribedActivities.size());
        model.addAttribute("activityCount", activityService.activityCount());
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("userRegister", user);

        //PAGEABLE:
        int page = 0;
        Page<Activity> activities = activityService.getActivitiesPaginated(page);
        model.addAttribute("allActivities", activities);
        return "admin_activities";
    }

    @GetMapping("/moreActivitiesAdmin") 
    public String loadMoreActivityAdmin(@RequestParam int page, Model model) { 
        System.out.println("Cargando usuarios, página: " + page);
    
        try {
            // Obtener el total de páginas disponibles
            int totalPages = activityService.getActivitiesPaginated(0).getTotalPages();
    
            // Si la página solicitada es mayor o igual al total, no hay más usuarios
            if (page >= totalPages) {
                model.addAttribute("allActivities", new ArrayList<>()); // No hay más usuarios
                model.addAttribute("hasMore", false);
                return "moreActivitiesAdmin";
            }
    
            Page<Activity> activities = activityService.getActivitiesPaginated(page);
    
            if (activities == null) {
                throw new RuntimeException("activityRepository.findAll(pageable) retornó null");
            }
    
            model.addAttribute("allActivities", activities.getContent()); // Asegurar que es "users"
            boolean hasMore = page < activities.getTotalPages() - 1;
            model.addAttribute("hasMore", hasMore);
    
            System.out.println("Usuarios cargados: " + activities.getContent().size());
            System.out.println("Total páginas: " + activities.getTotalPages());
            System.out.println("Has more activities: " + hasMore);
    
            return "moreActivitiesAdmin";
        } catch (Exception e) {
            System.err.println("Error en /moreActivitiesAdmin: " + e.getMessage());
            e.printStackTrace();
            return "errorPage";  // Página de error si hay un problema
        }
    }
    
        
    




    @GetMapping("/404")
    public String showError() {
        return "404";
    }
    @Transactional
    @PostMapping("/removeActivity")
    public String removeActivity(@RequestParam Long id,Model model) {
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
        model.addAttribute("activity", activity);
    
        return "activity";  // Nombre del archivo .mustache
    }


    @GetMapping("/activity/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

		Optional<Activity> optionalActivity = activityService.findById(id);
		if (optionalActivity.isPresent() && optionalActivity.get().getImageFile() != null) {

			Resource file = new InputStreamResource(optionalActivity.get().getImageFile().getBinaryStream());

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
					.contentLength(optionalActivity.get().getImageFile().length()).body(file);

		} else {
			return ResponseEntity.notFound().build();
		}
	}

    private void updateImage(Activity activity, boolean removeImage, MultipartFile imageField) throws IOException, SQLException {
		
		if (!imageField.isEmpty()) {
			activity.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
			activity.setImage(true);
		} else {
			if (removeImage) {
				activity.setImageFile(null);
				activity.setImage(false);
			} else {
				// Maintain the same image loading it before updating the book
				Activity dbActivity = activityService.findById(activity.getId()).orElseThrow();
				if (dbActivity.getImage()) {
					activity.setImageFile(BlobProxy.generateProxy(dbActivity.getImageFile().getBinaryStream(),
                    dbActivity.getImageFile().length()));
					activity.setImage(true);
				}
			}
		}
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
                activity.setImage(true);
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

        Optional<Activity> optionalActivity = activityService.findById(id);
        Activity activity = optionalActivity.get();

        if (optionalActivity.isEmpty()) {
            model.addAttribute("errorMessage", "Actividad no encontrada.");
            return "404";  
        }
        
        Place place = activity.getPlace();
        model.addAttribute("activity", activity);
        model.addAttribute("place", place);
        model.addAttribute("places", placeService.getAllPlaces());

        return "Edit_activity";  
    }

    @PostMapping("/editActivity/{id}")
    public String editActivity(@PathVariable Long id, @ModelAttribute Activity updatedActivity, @RequestParam("file") MultipartFile imagFile,@RequestParam(value = "removeImage", required = false, defaultValue = "false") boolean removeImage)
    throws IOException, SQLException {
        Optional<Activity> optionalActivity = activityService.findById(id);
        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();

            activity.setName(updatedActivity.getName());
            activity.setDescription(updatedActivity.getDescription());
            activity.setCategory(updatedActivity.getCategory());
            activity.setVacancy(updatedActivity.getVacancy());
            activity.setPlace(updatedActivity.getPlace());
            updateImage(activity, removeImage, imagFile);

            // Guarda los cambios
            activityService.saveActivity(activity);

            return "redirect:/admin_activities";  // Redirige a la vista de administración de actividades
        }

        return "404";  // Si no se encuentra la actividad, muestra un error
    }

    @GetMapping("/search_page")
    public String showSearchs(Model model, Principal principal,@RequestParam(value = "placeId", required = false) Long placeId) {
        // Verifica si no se ha enviado un placeId
        if (placeId == null) {
            return "redirect:/404";  // Redirige a la página 404 si no se seleccionó un lugar
        }
    

        Optional<Place> optionalPlace = placeRepository.findById(placeId);

        if (optionalPlace.isPresent()) {
            Place place = optionalPlace.get();
            List<Activity> activitiesByPlace = activityRepository.findByPlace(place);
             
            model.addAttribute("activitiesByPlace", activitiesByPlace); 
            model.addAttribute("placeName", place.getName());
        } else {
            return "404";  
        }
        return "search_page"; 
    }

   /*  @PostMapping("/activity/{id}/reserve")
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
    }*/

    @PostMapping("/activity/{id}/reserve")
    public ResponseEntity<byte[]> reserveActivity(@PathVariable Long id, Principal principal) throws IOException {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // Si el usuario no está autenticado, devolver 401
        }

        String userEmail = principal.getName();
        User user = userRepository.findByEmail(userEmail);

        if (user == null) {
            return ResponseEntity.status(404).build(); // Si el usuario no existe, devolver 404
        }

        boolean success = activityService.reserveActivity(id, user.getId());

        if (!success) {
            return ResponseEntity.status(400).build(); // Si la reserva falla, devolver 400 (error)
        }

        // Generar el PDF de la reserva
        byte[] pdfContents = activityService.generateReservationPDF(id, user.getId()).toByteArray();

        // Configurar los encabezados para la descarga
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Ticket_Reserva_" + user.getName() + ".pdf");

        // Devolver el PDF como respuesta
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfContents);
    }
}

