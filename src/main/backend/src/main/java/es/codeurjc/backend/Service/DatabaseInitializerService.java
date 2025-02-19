package es.codeurjc.backend.Service;


import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import es.codeurjc.backend.Model.Activity;


@Component
public class DatabaseInitializerService implements CommandLineRunner {

    private final ActivityService activityService;

    public DatabaseInitializerService(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear algunas actividades de prueba
        // Crear las actividades con todos los atributos
        Activity activity1 = new Activity(
            "Actividad 1", 
            "Descripción 1", 
            "Lugar 1", 
            "Categoría 1"
        );
        activity1.setDate("2025-02-20");  // Establecer fecha
        activity1.setHour("10:00 AM");   // Establecer hora
        activity1.setVacancy(30);        // Establecer vacantes

        Activity activity2 = new Activity(
            "Actividad 2", 
            "Descripción 2", 
            "Lugar 2", 
            "Categoría 2"
        );
        activity2.setDate("2025-02-21");  // Establecer fecha
        activity2.setHour("02:00 PM");   // Establecer hora
        activity2.setVacancy(50);        // Establecer vacantes

        // Guardarlas en la base de datos
        activityService.saveActivity(activity1);
        activityService.saveActivity(activity2);

        // Imprimir un mensaje de confirmación
        System.out.println("Datos de prueba cargados");
    }
}
