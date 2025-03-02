package es.codeurjc.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Model.Place;
import es.codeurjc.backend.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

import java.util.Set;


@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    Optional<Activity> findByName(String nombre);
    
    void deleteById( Long id);

    List<Activity> findByUsers(User user);
    
    List<Activity> findByPlace(Place place);

    
    Page<Activity> findAll( Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE (a.category IN :categories OR a.place IN :places) AND a NOT IN :userActivities")
    List<Activity> findSimilarActivities(@Param("categories") Set<String> categories, 
                                         @Param("places") Set<Place> places, 
                                         @Param("userActivities") List<Activity> userActivities);
    
    
}

