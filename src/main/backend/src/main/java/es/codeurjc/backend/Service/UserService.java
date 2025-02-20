package es.codeurjc.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.backend.Repository.UserRepository;

import es.codeurjc.backend.Model.User;


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

}
