package es.codeurjc.backend.controllers;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import es.codeurjc.backend.dto.ActivityDto;
import es.codeurjc.backend.model.Activity;
import es.codeurjc.backend.model.Place;
import es.codeurjc.backend.model.Review;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.service.ActivityService;
import es.codeurjc.backend.service.PlaceService;
import es.codeurjc.backend.service.ReviewService;
import es.codeurjc.backend.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

@Controller
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    @GetMapping("/")
    public Object showActivities(
            Model model,
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "false") boolean recommendedOnly,
            HttpServletRequest request,
            HttpServletResponse response) { 
    
        int size = 4;
        Pageable pageable = PageRequest.of(page, size);
        
        try {
            // Check if request is AJAX
            boolean isAjaxRequest = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
            
            if (principal != null) {
                String userEmail = principal.getName();
                User user = userService.findByEmail(userEmail);
                
                if (user != null) {
                    Page<ActivityDto> recommendedActivities = activityService.recommendActivities(user.getId(), pageable);
                    
                    if (isAjaxRequest) {
                        // Return JSON response for AJAX requests directly
                        response.setContentType("application/json");
                        Map<String, Object> jsonResponse = new HashMap<>();
                        
                        if (recommendedOnly) {
                            // If specifically requesting recommended activities
                            jsonResponse.put("activities", recommendedActivities.getContent());
                            jsonResponse.put("hasMore", recommendedActivities.hasNext());
                        } else {
                            // Regular activities
                            Page<ActivityDto> activities = activityService.getActivities(pageable);
                            jsonResponse.put("activities", activities.getContent());
                            jsonResponse.put("hasMore", activities.hasNext());
                        }
                        
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.writeValue(response.getWriter(), jsonResponse);
                        return null;
                    }
                    
                    // For non-AJAX requests, add recommended activities to the model
                    model.addAttribute("recommendedActivities", recommendedActivities.getContent());
                    model.addAttribute("hasMoreRecommended", recommendedActivities.hasNext());
                }
            }
            
            // Regular activities pagination for the main activities list
            Page<ActivityDto> activities = activityService.getActivities(pageable);
            model.addAttribute("activities", activities.getContent());
            model.addAttribute("hasMore", activities.hasNext());
            
            // Add places to the model
            List<Place> places = placeService.findAll();
            model.addAttribute("allPlaces", places);
            
            return "index";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/debug/activities")
    @ResponseBody
    public Map<String, Object> debugActivities(@RequestParam(defaultValue = "0") int page) {
        int size = 4;
        Pageable pageable = PageRequest.of(page, size);
        
        Page<ActivityDto> activities = activityService.getActivities(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("activities", activities.getContent());
        response.put("hasMore", activities.hasNext());
        response.put("totalElements", activities.getTotalElements());
        response.put("totalPages", activities.getTotalPages());
        response.put("currentPage", page);
        
        return response;
    }

    @GetMapping("/moreActivities")
    public String loadMoreActivities(
            @RequestParam int page, 
            Model model) {

        int size = 4;
        Pageable pageable = PageRequest.of(page, size);

        Page<ActivityDto> activities = activityService.getActivities(pageable);

        boolean hasMore = page < activities.getTotalPages() - 1;

        model.addAttribute("activities", activities.getContent());
        model.addAttribute("hasMore", hasMore);

        return "moreActivities";
    }

    @GetMapping("/adminActivities")
    public String showAdminActivities(Model model, HttpServletRequest request, Principal principal, 
                                    @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("user", request.isUserInRole("USER"));
        
        String userEmail = principal.getName();
        User user = userService.findByEmail(userEmail);
        
        int sizeSubscribed = 10;
        Pageable pageableSubscribed = PageRequest.of(0, sizeSubscribed);
        Page<ActivityDto> subscribedActivities = activityService.getActivitiesByUser(user.getId(), pageableSubscribed);
        
        model.addAttribute("countActivitiesSubscribed", subscribedActivities.getTotalElements());
        model.addAttribute("activityCount", activityService.activityCount());
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("userRegister", user);
        
        int sizeAll = 10;
        Pageable pageableAll = PageRequest.of(page, sizeAll);
        Page<ActivityDto> activities = activityService.getActivities(pageableAll);
        
        model.addAttribute("allActivities", activities.getContent()); 
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", activities.getTotalPages());
        
        return "adminActivities";
    }

    @GetMapping("/moreActivitiesAdmin")
    public String loadMoreActivityAdmin(
            @RequestParam int page, 
            Model model) {

        try {
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);

            int totalPages = activityService.getActivities(PageRequest.of(0, size)).getTotalPages();

            if (page >= totalPages) {
                model.addAttribute("allActivities", new ArrayList<>());
                model.addAttribute("hasMore", false);
                return "moreActivitiesAdmin";
            }

            Page<ActivityDto> activities = activityService.getActivities(pageable);

            if (activities == null) {
                throw new RuntimeException("activityService.getActivities(pageable) retornó null");
            }

            model.addAttribute("allActivities", activities.getContent());
            boolean hasMore = page < activities.getTotalPages() - 1;
            model.addAttribute("hasMore", hasMore);

            return "moreActivitiesAdmin";
        } catch (Exception e) {
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
    public String removeActivity(@RequestParam Long id, Model model) {
        try {
            activityService.deleteActivity(id);
            return "redirect:/adminActivities";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", "Actividad no encontrada con ID: " + id);
            return "error";
        }
    }

    @GetMapping("/activity/{id}")
    public String getActivityDetail(
            @PathVariable Long id,
            Model model,
            Principal principal) {

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
            model.addAttribute("register", true);

            Pageable pageable = PageRequest.of(0, 10); 
            Page<ActivityDto> subscribedActivities = activityService.getActivitiesByUser(user.getId(), pageable);

            boolean isSubscribed = subscribedActivities.getContent().stream()
                    .anyMatch(subscribedActivity -> subscribedActivity.id().equals(activity.getId()));

            model.addAttribute("isSubscribed", isSubscribed);
        } else {
            model.addAttribute("register", false);
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
        return "createActivity";
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
            return "redirect:/adminActivities";
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

        return "editActivity";  
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

            return "redirect:/adminActivities";  
        }

        return "error";  
    }

    @GetMapping("/searchPage")
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
        return "searchPage"; 
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
