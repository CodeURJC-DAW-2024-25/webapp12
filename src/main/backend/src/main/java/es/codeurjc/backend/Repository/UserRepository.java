package es.codeurjc.backend.Repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.backend.Model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;








@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    User findByEmail(String email);
    Optional<User> findById(Long id);
    void deleteById( Long id);
    boolean existsByEmail(String email);

    Page<User> findAll(Pageable pageable);
}
