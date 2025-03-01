package es.codeurjc.backend.Repository;



import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.backend.Model.Place;


@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Place findByName(String name);
    Optional<Place> findById(Long id);
    
}
