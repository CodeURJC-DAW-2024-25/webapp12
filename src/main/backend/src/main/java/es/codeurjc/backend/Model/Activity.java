package es.codeurjc.backend.Model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Esta anotación genera el ID automáticamente
    private Long id;

    private String activityName;
    private String activityDescription;
    private String place;
    private String date;
    private String hour;
    private int vacancy;
    private String category;

    public Activity(String activityName, String activityDescription, String place, String category) {
        this.activityName = activityName;
        this.activityDescription = activityDescription;
        this.place = place;
        this.category = category;
        
    }

    public Activity() {
    }

    // Constructor con parámetros (opcional)
    public Activity(String activityName, String activityDescription) {
        this.activityName = activityName;
        this.activityDescription = activityDescription;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int getVacancy() {
        return vacancy;
    }

    public void setVacancy(int vacancy) {
        this.vacancy = vacancy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
