package es.codeurjc.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.backend.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    void deleteById(long id);

    List<Review> findByActivity_Id(Long id);
    Page<Review> findAll( Pageable pageable);
    Page<Review> findByActivityId(Long activityId, Pageable pageable);

    
}