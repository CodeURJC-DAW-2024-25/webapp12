package es.codeurjc.backend;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nameUser;
    private String description;
    private int score;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private model user;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;
}
