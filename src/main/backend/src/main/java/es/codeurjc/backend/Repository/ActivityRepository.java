package es.codeurjc.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.codeurjc.backend.model.Activity;
import es.codeurjc.backend.model.Place;
import es.codeurjc.backend.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

import java.util.Set;


@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    Optional<Activity> findByName(String nombre);
    
    void deleteById( Long id);

    List<Activity> findByUsers(User user);
    Page<Activity> findByUsers(Pageable pageable, User user);
    
    List<Activity> findByPlace(Place place);

    
    Page<Activity> findAll( Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE (a.category IN :categories OR a.place IN :places) AND a NOT IN :userActivities")
    List<Activity> findSimilarActivities(@Param("categories") Set<String> categories, 
                                         @Param("places") Set<Place> places, 
                                         @Param("userActivities") List<Activity> userActivities);
    
    @Query("SELECT a FROM Activity a LEFT JOIN FETCH a.reviews")
    List<Activity> findAllWithReviews();

    @Query("SELECT a FROM Activity a LEFT JOIN FETCH a.reviews WHERE a.id = :id")
    Optional<Activity> findByIdWithReviews(@Param("id") Long id);

    @Query("SELECT a FROM Activity a JOIN FETCH a.reviews")
    Page<Activity> findAllWithReviews(Pageable pageable);

    Page<Activity> findByPlace(Place place, Pageable pageable);
}

