package es.codeurjc.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.backend.Repository.ReviewRepository;

@Service
public class ReviewService {
     @Autowired
    private ReviewRepository reviewRepository; 
    
    public void delete(long id){
        reviewRepository.deleteById(id);
    }
}
