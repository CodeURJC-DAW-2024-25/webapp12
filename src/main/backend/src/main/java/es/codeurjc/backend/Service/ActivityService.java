package es.codeurjc.backend.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.awt.Color;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.util.StringUtils;
import java.util.Objects;

import java.util.Optional;

import es.codeurjc.backend.dto.ActivityDto;
import es.codeurjc.backend.dto.ActivityMapper;
import es.codeurjc.backend.dto.ActivityUpdateDto;
import es.codeurjc.backend.dto.NewActivityDto;
import es.codeurjc.backend.dto.PlaceDto;
import es.codeurjc.backend.dto.ReviewDto;
import es.codeurjc.backend.model.Activity;
import es.codeurjc.backend.model.Place;
import es.codeurjc.backend.model.Review;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.repository.ActivityRepository;
import es.codeurjc.backend.repository.PlaceRepository;
import es.codeurjc.backend.repository.ReviewRepository;
import es.codeurjc.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository; 
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private ReviewRepository reviewRepository;


    
    public Activity saveActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public Optional <Activity>  findById(long id){
        return activityRepository.findById(id);
    } 
         
    public Map<Integer, Long> countActivitiesByMonth() {
        List<Activity> activities = activityRepository.findAll();
        Map<Integer, Long> activitiesByMonth = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            activitiesByMonth.put(i, 0L);
        }

        for (Activity activity : activities) {
            Date activityDate = activity.getActivityDate();
            if (activityDate != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(activityDate);
                int month = calendar.get(Calendar.MONTH) + 1; 

                activitiesByMonth.put(month, activitiesByMonth.get(month) + 1);
            }
        }

        return activitiesByMonth;
    }

    public ByteArrayOutputStream generateReservationPDF(Long activityId, Long userId) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
    
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
    
                
                float boxWidth = page.getMediaBox().getWidth() - 2 * margin;
                float boxHeight = 250; 
                float boxX = margin;
                float boxY = yStart - boxHeight - 30;
    
                contentStream.setLineWidth(1);
                contentStream.setStrokingColor(Color.BLACK);
                contentStream.setNonStrokingColor(new Color(200, 220, 255)); 
                contentStream.addRect(boxX, boxY, boxWidth, boxHeight);
                contentStream.fill();
                contentStream.setNonStrokingColor(Color.BLACK); 
    
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.setNonStrokingColor(new Color(0, 102, 204));
                contentStream.beginText();
                PDFont font = PDType1Font.HELVETICA_BOLD;
                float titleWidth = (font.getStringWidth("Ticket de Reserva") / 1000) * 18;
                contentStream.newLineAtOffset((page.getMediaBox().getWidth() - titleWidth) / 2, yStart);
                contentStream.showText("Ticket de Reserva");
                contentStream.endText();
    
                
                Activity activity = activityRepository.findById(activityId).orElse(null);
                User user = userRepository.findById(userId).orElse(null);
    
                if (activity != null && user != null) {
                    
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.setNonStrokingColor(new Color(50, 50, 50)); 
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 20, yStart - 50);
                    contentStream.showText("Usuario: ");
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.setNonStrokingColor(new Color(0, 102, 204)); 
                    contentStream.showText(user.getName());
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.setNonStrokingColor(new Color(50, 50, 50)); 
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("Actividad: ");
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.setNonStrokingColor(new Color(0, 102, 204)); 
                    contentStream.showText(activity.getName());
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.setNonStrokingColor(new Color(50, 50, 50)); 
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("Vacantes restantes: " + activity.getVacancy());
                    contentStream.endText();
    
                    contentStream.setLineWidth(1.5f);
                    contentStream.setStrokingColor(new Color(0, 102, 204)); 
                    contentStream.moveTo(margin, yStart - 100);
                    contentStream.lineTo(page.getMediaBox().getWidth() - margin, yStart - 100);
                    contentStream.stroke();
                }
            }
    
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream;
        }
    }
    @Transactional
    public boolean reserveActivity(Long activityId, Long userId) {
      
        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null) {
            return false; 
        }
       
        if (activity.getVacancy() <= 0) {
            return false; 
        }
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false; 
        }
        
        if (user.getActivities().contains(activity)) {
            return false; 
        }
       
        activity.setVacancy(activity.getVacancy() - 1);              
        activityRepository.save(activity);       
        user.getActivities().add(activity);     
        userRepository.save(user);

        return true; 
    }

    public List<Activity> findByPlace(Place place) {
        return activityRepository.findByPlace(place);
    }

    public boolean exists(Long id) {
        return activityRepository.existsById(id);
    }

    public long activityCount(){
        return activityRepository.count();
    }

    private ActivityDto convertToDto(Activity activity) {
        return new ActivityDto(
            activity.getId(),
            activity.getName(),
            activity.getCategory(),
            activity.getDescription(),
            activity.getVacancy(),
            activity.getCreationDate(),
            activity.getActivityDate(),
            activity.getPlace() != null ? 
                new PlaceDto(
                    activity.getPlace().getId(),
                    activity.getPlace().getName(),
                    activity.getPlace().getDescription()
                ) : null,
            activity.getReviews().stream()
                .map(review -> new ReviewDto(
                    review.getId(),
                    review.getDescription(),
                    review.getStarsValue(),
                    review.getCreationDate(),
                    review.getUserFullName()
                ))
                .toList(),
            activity.getImage()
        );
    }

    @Transactional
    public Collection<ActivityDto> getActivitiesDtos() {
        return activityMapper.toDTOs(activityRepository.findAllWithReviews());
    } 

    @Transactional
    public ActivityDto getActivityDtoById(Long id) {
        Activity activity = activityRepository.findByIdWithReviews(id)
            .orElseThrow(() -> new EntityNotFoundException("Actividad no encontrada con ID: " + id));
        return activityMapper.toDto(activity);
    }
        
    @Transactional
    public ActivityDto createActivity(NewActivityDto activityPostDto) {
        if (activityPostDto == null ||
            !StringUtils.hasText(activityPostDto.name()) ||
            !StringUtils.hasText(activityPostDto.category()) ||
            !StringUtils.hasText(activityPostDto.description()) ||
            Objects.isNull(activityPostDto.vacancy()) ||
            Objects.isNull(activityPostDto.placeId()) ||
            Objects.isNull(activityPostDto.activityDate())) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }

        Place place = placeRepository.findById(activityPostDto.placeId())
            .orElseThrow(() -> new EntityNotFoundException("Lugar no encontrado con ID: " + activityPostDto.placeId()));

        Activity activity = new Activity();
        activity.setName(activityPostDto.name());
        activity.setCategory(activityPostDto.category());
        activity.setDescription(activityPostDto.description());
        activity.setVacancy(activityPostDto.vacancy());
        activity.setPlace(place);
        activity.setCreationDate(Calendar.getInstance());

        java.sql.Date sqlDate = java.sql.Date.valueOf(activityPostDto.activityDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        activity.setActivityDate(sqlDate);

        Activity savedActivity = activityRepository.save(activity);
        return activityMapper.toDto(savedActivity);
    }

    @Transactional
    public ActivityDto updateActivity(Long id, ActivityUpdateDto activityUpdateDto) {
        Activity activity = activityRepository.findByIdWithReviews(id)
            .orElseThrow(() -> new EntityNotFoundException("Actividad no encontrada con ID: " + id));
    
        if (activityUpdateDto.getName() != null) {
            activity.setName(activityUpdateDto.getName());
        }
        if (activityUpdateDto.getCategory() != null) {
            activity.setCategory(activityUpdateDto.getCategory());
        }
        if (activityUpdateDto.getDescription() != null) {
            activity.setDescription(activityUpdateDto.getDescription());
        }
        if (activityUpdateDto.getVacancy() != null) {
            activity.setVacancy(activityUpdateDto.getVacancy());
        }    
        if (activityUpdateDto.getActivityDate() != null) {
            java.sql.Date sqlDate = new java.sql.Date(activityUpdateDto.getActivityDate().getTime());
            activity.setActivityDate(sqlDate);
        }        
        if (activityUpdateDto.getPlaceId() != null) {
            Place place = placeRepository.findById(activityUpdateDto.getPlaceId())
                .orElseThrow(() -> new EntityNotFoundException("Lugar no encontrado"));
            activity.setPlace(place);
        }
    
        Activity updatedActivity = activityRepository.save(activity);
        return convertToDto(updatedActivity);
    }  

    @Transactional
    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Actividad no encontrada con ID: " + id));

        // Desvincular las reviews de la actividad
        List<Review> reviews = new ArrayList<>(activity.getReviews());
        activity.getReviews().clear();
        reviewRepository.deleteAll(reviews);

        // Desvincular los usuarios de la actividad
        List<User> users = new ArrayList<>(activity.getUsers());
        for (User user : users) {
            user.getActivities().remove(activity);
            userRepository.save(user); // Guardar los cambios en el usuario
        }
        activity.getUsers().clear();

        // Finalmente, eliminar la actividad
        activityRepository.delete(activity);
    }

    public void save(Activity activity) {
        activityRepository.save(activity);
    }
    
    @Transactional 
    public Page<ActivityDto> getActivities(Pageable pageable) {
        Page<Activity> activitiesPage = activityRepository.findAll(pageable);

        return activitiesPage.map(activity -> new ActivityDto(
            activity.getId(),
            activity.getName(),
            activity.getCategory(),
            activity.getDescription(),
            activity.getVacancy(),
            activity.getCreationDate(),
            activity.getActivityDate(),
            new PlaceDto(activity.getPlace().getId(), activity.getPlace().getName(), activity.getPlace().getDescription()),
            activity.getReviews().stream()
                .map(review -> new ReviewDto(
                    review.getId(),
                    review.getDescription(),
                    review.getStarsValue(),
                    review.getCreationDate(),
                    review.getUserFullName()
                ))
                .toList(),
            activity.getImage()
        ));
    }

    @Transactional
    public Page<ActivityDto> findByPlace(Place place, Pageable pageable) {
        Page<Activity> activitiesPage = activityRepository.findByPlace(place, pageable);

        return activitiesPage.map(activity -> new ActivityDto(
            activity.getId(),
            activity.getName(),
            activity.getCategory(),
            activity.getDescription(),
            activity.getVacancy(),
            activity.getCreationDate(),
            activity.getActivityDate(),
            new PlaceDto(activity.getPlace().getId(), activity.getPlace().getName(), activity.getPlace().getDescription()),
            activity.getReviews().stream()
                .map(review -> new ReviewDto(
                    review.getId(),
                    review.getDescription(),
                    review.getStarsValue(),
                    review.getCreationDate(),
                    review.getUserFullName()
                ))
                .toList(),
            activity.getImage()
        ));
    }

    @Transactional
    public Page<ActivityDto> getActivitiesByUser(Long userId, Pageable pageable) {
        // Obtener las actividades paginadas del repositorio
        Page<Activity> activitiesPage = activityRepository.findByUsers(userId, pageable);

        // Convertir las actividades a DTOs
        return activitiesPage.map(activity -> new ActivityDto(
                activity.getId(),
                activity.getName(),
                activity.getCategory(),
                activity.getDescription(),
                activity.getVacancy(),
                activity.getCreationDate(),
                activity.getActivityDate(),
                new PlaceDto(
                    activity.getPlace().getId(),
                    activity.getPlace().getName(),
                    activity.getPlace().getDescription()
                ),
                activity.getReviews().stream()
                    .map(review -> new ReviewDto(
                        review.getId(),
                        review.getDescription(),
                        review.getStarsValue(),
                        review.getCreationDate(),
                        review.getUserFullName()
                    ))
                    .toList(),
                activity.getImage()
        ));
    }


    @Transactional
    public Page<ActivityDto> recommendActivities(Long userId, Pageable pageable) {
        // Obtener el usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener las actividades del usuario
        List<Activity> userActivities = new ArrayList<>(user.getActivities());

        // Obtener las categorías y lugares de las actividades del usuario
        Set<String> categories = userActivities.stream()
                .map(Activity::getCategory)
                .collect(Collectors.toSet());

        Set<Place> places = userActivities.stream()
                .map(Activity::getPlace)
                .collect(Collectors.toSet());

        // Obtener actividades recomendadas paginadas
        Page<Activity> recommendedActivities = activityRepository.findSimilarActivities(categories, places, userActivities, pageable);

        // Convertir las actividades a DTOs
        return recommendedActivities.map(activity -> new ActivityDto(
                activity.getId(),
                activity.getName(),
                activity.getCategory(),
                activity.getDescription(),
                activity.getVacancy(),
                activity.getCreationDate(),
                activity.getActivityDate(),
                new PlaceDto(
                    activity.getPlace().getId(),
                    activity.getPlace().getName(),
                    activity.getPlace().getDescription()
                ),
                activity.getReviews().stream()
                    .map(review -> new ReviewDto(
                        review.getId(),
                        review.getDescription(),
                        review.getStarsValue(),
                        review.getCreationDate(),
                        review.getUserFullName()
                    ))
                    .toList(),
                activity.getImage()
        ));
    }

}




