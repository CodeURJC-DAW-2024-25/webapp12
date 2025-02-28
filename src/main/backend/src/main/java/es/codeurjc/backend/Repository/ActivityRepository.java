package es.codeurjc.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    Optional<Activity> findByName(String nombre);
    
    void deleteById(Long id);

    List<Activity> findByUsers(User user);

    Page<Activity> findAll(Pageable pageable);
}

