package es.codeurjc.backend.Repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.backend.Model.User;
import io.micrometer.common.lang.NonNull;







@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    User findByEmail(String email);
    Optional<User> findById(Long id);
    void deleteById( Long id);
    boolean existsByEmail(String email);
}
