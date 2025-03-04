package es.codeurjc.backend.Service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.File;

//import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Model.Place;
import es.codeurjc.backend.Model.User;
import es.codeurjc.backend.Repository.ActivityRepository;
import es.codeurjc.backend.Repository.UserRepository;
import jakarta.transaction.Transactional;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
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

    public Page<Activity> getAllActivities(Pageable pageable) {
        return activityRepository.findAll(pageable);
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
    // Crear un documento PDF en memoria
    PDDocument document = new PDDocument();
    
    // Crear una nueva página
    PDPage page = new PDPage();
    document.addPage(page);

    // Obtener el contenido de la página
    PDPageContentStream contentStream = new PDPageContentStream(document, page);

    // Establecer la fuente y tamaño de texto
    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
    contentStream.beginText();
    
    // Establecer la posición de inicio del texto
    contentStream.newLineAtOffset(50, 750);

    // Buscar la actividad y el usuario desde la base de datos
    Activity activity = activityRepository.findById(activityId).orElse(null);
    User user = userRepository.findById(userId).orElse(null);

    // Verificar que la actividad y el usuario existan
    if (activity != null && user != null) {
        // Agregar contenido al PDF
        contentStream.showText("Ticket de Reserva");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Usuario: " + user.getName());
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Actividad: " + activity.getName());
        contentStream.newLineAtOffset(0, -20);
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Vacantes restantes: " + activity.getVacancy());
    }

    contentStream.endText();
    contentStream.close();

    // Guardar el documento en un ByteArrayOutputStream
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    document.save(byteArrayOutputStream);
    document.close();

    // Retornar el contenido del PDF como un ByteArrayOutputStream
    return byteArrayOutputStream;
}
    @Transactional
    public boolean reserveActivity(Long activityId, Long userId) {
        // Buscar la actividad
        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null) {
            return false; // Si no se encuentra la actividad, retornar false
        }

        // Verificar si hay vacantes disponibles
        if (activity.getVacancy() <= 0) {
            return false; // Si no hay vacantes disponibles, retornar false
        }

        // Buscar el usuario
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false; // Si no se encuentra el usuario, retornar false
        }

        // Añadir la actividad al usuario si no está ya inscrito
        if (user.getActivities().contains(activity)) {
            return false; // Si el usuario ya está inscrito en la actividad, retornar false
        }

        // Disminuir el número de vacantes disponibles
        activity.setVacancy(activity.getVacancy() - 1);
        
        // Guardar la actividad con las nuevas vacantes
        activityRepository.save(activity);

        // Agregar la actividad al usuario
        user.getActivities().add(activity);
        
        // Guardar el usuario con la nueva actividad reservada
        userRepository.save(user);

        return true; // Reserva exitosa
    }
}




