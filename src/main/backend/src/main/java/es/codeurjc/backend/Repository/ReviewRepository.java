package es.codeurjc.backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.backend.Model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    void deleteById(long id);

    List<Review> findByActivity_Id(Long id);
    
}