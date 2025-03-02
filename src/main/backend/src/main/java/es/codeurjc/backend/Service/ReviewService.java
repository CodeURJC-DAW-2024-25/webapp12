package es.codeurjc.backend.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

    public List<Integer> countReviewsByValoration(){
        List<Review> reviews = reviewRepository.findAll();
        List<Integer> reviewListValoration = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0)); 
        for (Review review : reviews) {
            int valoration = review.getStarsValue(); 
            if (valoration >= 1 && valoration <= 5) {
                reviewListValoration.set(valoration - 1, reviewListValoration.get(valoration - 1) + 1); 
            }
        }
        return reviewListValoration; 
    }
}
