package es.codeurjc.backend;

import jakarta.persistence.*;
import java.util.Set;


@Entity
public class model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String dni;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String password;

    @OneToMany(mappedBy = "user")
    private Set<Review> reviews;
    
    @ManyToMany
    @JoinTable(
        name = "enroll",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    private Set<Activity> activities;
}



