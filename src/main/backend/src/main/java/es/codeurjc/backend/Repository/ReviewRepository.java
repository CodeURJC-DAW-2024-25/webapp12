package es.codeurjc.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.backend.Model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}