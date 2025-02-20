package es.codeurjc.backend;

import java.util.Set;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import jakarta.persistence.*;



@Entity
public class place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String namePlace;
    private String description;
    private String category;

    @OneToMany(mappedBy = "place")
    private Set<Activity> activities;
}

