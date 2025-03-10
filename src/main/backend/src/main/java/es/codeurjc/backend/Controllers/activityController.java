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
    private UserService userService;



    @Autowired
    private PlaceService placeService;

    @GetMapping("/")
    public String showActivities(Model model, Principal principal) {
        int page = 0;
        Page<Activity> activities = activityService.getActivitiesPaginated(page);

        
        List<Place> places = placeService.findAll();
        model.addAttribute("activities", activities);
        model.addAttribute("allPlaces", places);
        
        
        if (principal != null) {
            String userEmail = principal.getName();
            System.out.println("Usuario autenticado: " + userEmail);

            
            User user = userService.findByEmail(userEmail);
            if (user != null) {
                 
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
        User user  = userService.findByEmail(userEmail); 

        
        List<Activity> subscribedActivities = activityService.findEventsSubscribe(user);

        
        model.addAttribute("countActivitiesSubscribed", subscribedActivities.size());
        model.addAttribute("activityCount", activityService.activityCount());
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("userRegister", user);

        int page = 0;
        Page<Activity> activities = activityService.getActivitiesPaginated(page);
        model.addAttribute("allActivities", activities);
        return "admin_activities";
    }

    @GetMapping("/moreActivitiesAdmin") 
    public String loadMoreActivityAdmin(@RequestParam int page, Model model) { 
        System.out.println("Cargando usuarios, página: " + page);
    
        try {
            int totalPages = activityService.getActivitiesPaginated(0).getTotalPages();
    
            if (page >= totalPages) {
                model.addAttribute("allActivities", new ArrayList<>()); 
                model.addAttribute("hasMore", false);
                return "moreActivitiesAdmin";
            }
    
            Page<Activity> activities = activityService.getActivitiesPaginated(page);
    
            if (activities == null) {
                throw new RuntimeException("activityService.findAll(pageable) retornó null");
            }
    
            model.addAttribute("allActivities", activities.getContent()); 
            boolean hasMore = page < activities.getTotalPages() - 1;
            model.addAttribute("hasMore", hasMore);
    
            System.out.println("Usuarios cargados: " + activities.getContent().size());
            System.out.println("Total páginas: " + activities.getTotalPages());
            System.out.println("Has more activities: " + hasMore);
    
            return "moreActivitiesAdmin";
        } catch (Exception e) {
            System.err.println("Error en /moreActivitiesAdmin: " + e.getMessage());
            e.printStackTrace();
            return "errorPage";  
        }
    }
    
        
    




    @GetMapping("/error")
    public String showError() {
        return "error";
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
            return "error";
        }
        
    }

    @GetMapping("/activity/{id}")
    public String getActivityDetail(@PathVariable Long id, Model model, Principal principal) {
        Optional<Activity> optionalActivity = activityService.findById(id);
        
        if (optionalActivity.isEmpty()) {
            model.addAttribute("errorMessage", "Actividad no encontrada.");
            return "error"; 
        }
    
        Activity activity = optionalActivity.get();
        
        

        int page = 0;
        Page<Review> reviews = reviewService.getReviewsPaginated(id, page);
        model.addAttribute("reviews", reviews);
    
        
        Place place = activity.getPlace(); 
        model.addAttribute("place", place);
        
        
        if (principal != null) {
            String userEmail = principal.getName();
            User user = userService.findByEmail(userEmail);
    
            
            List<Activity> subscribedActivities = activityService.findEventsSubscribe(user);
            boolean isSubscribed = subscribedActivities.contains(activity);  
            
            model.addAttribute("isSubscribed", isSubscribed);
        }
        model.addAttribute("activity", activity);
    
        return "activity";  
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
				
				Activity dbActivity = activityService.findById(activity.getId()).orElseThrow();
				if (dbActivity.getImage()) {
					activity.setImageFile(BlobProxy.generateProxy(dbActivity.getImageFile().getBinaryStream(),
                    dbActivity.getImageFile().length()));
					activity.setImage(true);
				}
			}
		}
	}

    @GetMapping("/moreReviews")
    public String loadMoreReviews(@RequestParam Long activityId, @RequestParam int page, Model model) {
        Page<Review> reviews = reviewService.getReviewsPaginated(activityId, page);
        
        
        Optional<Activity> optionalActivity = activityService.findById(activityId);
        if (optionalActivity.isEmpty()) {
            model.addAttribute("errorMessage", "Actividad no encontrada.");
            return "error"; 
        }

        Activity activity = optionalActivity.get();
        model.addAttribute("activity", activity);  
        
        
        boolean hasMore = page < reviews.getTotalPages() - 1;
        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("hasMore", hasMore);

        return "moreReviews";  
    }

    
    
    
    
    @GetMapping("/createActivity")
    public String showFormNewActivity(Model model) {

        model.addAttribute("allPlaces", placeService.getAllPlaces());
        return "create_activity";
    }

    @PostMapping("/createActivity")
    public String createNewActivity(@ModelAttribute Activity activity, @RequestParam("placeId") Long placeId, @RequestParam("file") MultipartFile imagFile) {
        try {
            if (!imagFile.isEmpty()) {
                activity.setImageFile(BlobProxy.generateProxy(imagFile.getInputStream(), imagFile.getSize()));
                activity.setImage(true);
            }

            activity.setCreationDateMethod();

            
            Optional<Place> optionalPlace = placeService.findById(placeId);
            if (optionalPlace.isPresent()) {
                Place place = optionalPlace.get();
                activity.setPlace(place);
            } else {
                return "error"; 
            }

            
            java.util.Date utilDate = activity.getActivityDate();
            if (utilDate != null) {
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                activity.setActivityDate(sqlDate); 
            }

            
            activityService.saveActivity(activity);
            return "redirect:/admin_activities";
        } catch (IOException e) {
            e.printStackTrace();
            return "error"; 
        }
    }

    
    @GetMapping("/editActivity/{id}")
    public String showEditActivityForm(@PathVariable Long id, Model model) {

        Optional<Activity> optionalActivity = activityService.findById(id);
        Activity activity = optionalActivity.get();

        if (optionalActivity.isEmpty()) {
            model.addAttribute("errorMessage", "Actividad no encontrada.");
            return "error";  
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

            
            activityService.saveActivity(activity);

            return "redirect:/admin_activities";  
        }

        return "error";  
    }

    @GetMapping("/search_page")
    public String showSearchs(Model model, Principal principal,@RequestParam(value = "placeId", required = false) Long placeId) {
        
        if (placeId == null) {
            return "redirect:/error";  
        }
    

        Optional<Place> optionalPlace = placeService.findById(placeId);

        if (optionalPlace.isPresent()) {
            Place place = optionalPlace.get();
            List<Activity> activitiesByPlace = activityService.findByPlace(place);
             
            model.addAttribute("activitiesByPlace", activitiesByPlace); 
            model.addAttribute("placeName", place.getName());
        } else {
            return "error";  
        }
        return "search_page"; 
    }

    @PostMapping("/activity/{id}/reserve")
    public ResponseEntity<byte[]> reserveActivity(@PathVariable Long id, Principal principal) throws IOException {
        if (principal == null) {
            return ResponseEntity.status(401).build(); 
        }

        String userEmail = principal.getName();
        User user = userService.findByEmail(userEmail);

        if (user == null) {
            return ResponseEntity.status(404).build(); 
        }

        boolean success = activityService.reserveActivity(id, user.getId());

        if (!success) {
            return ResponseEntity.status(400).build(); 
        }

        byte[] pdfContents = activityService.generateReservationPDF(id, user.getId()).toByteArray();

        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Ticket_Reserva_" + user.getName() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfContents);
    }
}

