package es.codeurjc.backend.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.backend.Model.Review;
import es.codeurjc.backend.Repository.ReviewRepository;

@Service
public class ReviewService {
     @Autowired
    private ReviewRepository reviewRepository; 
    
    public void delete(long id){
        reviewRepository.deleteById(id);
    }

    public List<Review> findByActivity_Id(Long id) {
        return reviewRepository.findByActivity_Id(id);

    }

    public void save(Review review) {
        reviewRepository.save(review);
    }
}
