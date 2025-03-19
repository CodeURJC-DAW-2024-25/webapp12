package es.codeurjc.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.backend.dto.UserDto;
import es.codeurjc.backend.dto.UserMapper;
import es.codeurjc.backend.dto.UserUpdateDto;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.repository.UserRepository;
import es.codeurjc.backend.security.CSRFHandlerConfiguration;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final CSRFHandlerConfiguration CSRFHandlerConfiguration;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    UserService(CSRFHandlerConfiguration CSRFHandlerConfiguration) {
        this.CSRFHandlerConfiguration = CSRFHandlerConfiguration;
    }

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

    public User findByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    private Collection<UserDto> toDTOs(Collection<User> users) {
		return userMapper.toDTOs(users);
	}

    private UserDto toDTO(User user){
        return userMapper.toDto(user);
    }

    public Collection<UserDto> getUsersDtos() {

		return toDTOs(userRepository.findAll());
	}

    public UserDto getUserDto(Long id){
        return toDTO(userRepository.findById(id).orElseThrow());
    }

    public UserDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        userMapper.updateUserFromDto(userUpdateDto, user);
        userRepository.save(user);
        
        return userMapper.toDto(user);
    }

    public Resource getUserImageDto(Long id) throws SQLException{
        User user = userRepository.findById(id).orElseThrow();
        if(user.getImageFile() != null){
            return new InputStreamResource(user.getImageFile().getBinaryStream());
        }else{
            throw new NoSuchElementException();
        }
    }
    
}
