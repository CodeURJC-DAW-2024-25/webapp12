package es.codeurjc.backend;

import java.util.Set;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import jakarta.persistence.*;

import es.codeurjc.backend.Place;

@EntityScanty
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String namePlace;
    private String description;
    private String category;

    @OneToMany(mappedBy = "place")
    private Set<Activity> activities;
}

}
