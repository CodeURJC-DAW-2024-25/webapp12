package es.codeurjc.backend.Service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.File;
import java.awt.Color;

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
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
    
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Definir márgenes
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
    
                // Dibujar un rectángulo para enmarcar el contenido con color de fondo
                float boxWidth = page.getMediaBox().getWidth() - 2 * margin;
                float boxHeight = 250; // Mayor espacio para incluir más detalles
                float boxX = margin;
                float boxY = yStart - boxHeight - 30;
    
                contentStream.setLineWidth(1);
                contentStream.setStrokingColor(Color.BLACK);
                contentStream.setNonStrokingColor(new Color(200, 220, 255)); // Color de fondo azul claro
                contentStream.addRect(boxX, boxY, boxWidth, boxHeight);
                contentStream.fill();
                contentStream.setNonStrokingColor(Color.BLACK); // Restablecer color de texto
    
                // Configurar fuente y escribir el título centrado con color
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.setNonStrokingColor(new Color(0, 102, 204)); // Título en azul
                contentStream.beginText();
                PDFont font = PDType1Font.HELVETICA_BOLD;
                float titleWidth = (font.getStringWidth("Ticket de Reserva") / 1000) * 18;
                contentStream.newLineAtOffset((page.getMediaBox().getWidth() - titleWidth) / 2, yStart);
                contentStream.showText("Ticket de Reserva");
                contentStream.endText();
    
                // Buscar actividad y usuario
                Activity activity = activityRepository.findById(activityId).orElse(null);
                User user = userRepository.findById(userId).orElse(null);
    
                if (activity != null && user != null) {
                    // Detalles del usuario y actividad
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.setNonStrokingColor(new Color(50, 50, 50)); // Color gris oscuro para el texto principal
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 20, yStart - 50);
                    contentStream.showText("Usuario: ");
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.setNonStrokingColor(new Color(0, 102, 204)); // Azul para el nombre del usuario
                    contentStream.showText(user.getName());
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.setNonStrokingColor(new Color(50, 50, 50)); // Volver al gris para la siguiente línea
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("Actividad: ");
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.setNonStrokingColor(new Color(0, 102, 204)); // Azul para el nombre de la actividad
                    contentStream.showText(activity.getName());
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.setNonStrokingColor(new Color(50, 50, 50)); // Volver al gris para la siguiente línea
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("Vacantes restantes: " + activity.getVacancy());
                    contentStream.endText();
    
                    // Línea decorativa
                    contentStream.setLineWidth(1.5f);
                    contentStream.setStrokingColor(new Color(0, 102, 204)); // Azul para la línea
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




