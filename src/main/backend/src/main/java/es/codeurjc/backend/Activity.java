package es.codeurjc.backend;
import jakarta.persistence.*;
import java.util.Set;
@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String activityName;
    private String activityDescription;
    private String date;
    private String hour;
    private int vacancy;
    private String category;

    @OneToMany(mappedBy = "activity")
    private Set<Review> reviews;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;
}
