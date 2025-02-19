package es.codeurjc.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.backend.Model.Activity;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    // Puedes agregar métodos adicionales si lo necesitas
}

