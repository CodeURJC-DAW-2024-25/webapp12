package es.codeurjc.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.backend.Repository.UserRepository;
import es.codeurjc.backend.Model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    public List<User> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users;
    }

    public long countUsers(){
        return userRepository.count();
    }

    public void save(User user){
        userRepository.save(user);
    }

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> getUsersPaginated(int page) {
        int pageSize = 2; 
        Pageable pageable = PageRequest.of(page, pageSize);
    
        Page<User> users = userRepository.findAll(pageable);
        
        if (users == null) {
            throw new RuntimeException("userRepository.findAll(pageable) retornó null");
        }
        
        return users;
    }
    
    
}
