package es.codeurjc.backend.Repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.backend.Model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    User findByEmail(String email);
    
    



    
}
