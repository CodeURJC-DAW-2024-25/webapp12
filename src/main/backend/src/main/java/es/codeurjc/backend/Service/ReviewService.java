package es.codeurjc.backend.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.backend.Model.Activity;
import es.codeurjc.backend.Model.Review;
import es.codeurjc.backend.Model.User;
import es.codeurjc.backend.Repository.ActivityRepository;
import es.codeurjc.backend.Repository.ReviewRepository;
import es.codeurjc.backend.Repository.UserRepository;

@Service
public class ReviewService {
     @Autowired
    private ReviewRepository reviewRepository; 

     @Autowired
    private ActivityRepository ActivityRepository; 

    @Autowired
    private UserRepository UserRepository;
    
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
    public void saveReview(Long activityId, int starsValue, String description, Long userId) {
        Optional<Activity> activityOpt = ActivityRepository.findById(activityId);
        Optional<User> userOpt = UserRepository.findById(userId); // Suponiendo un usuario autenticado

        if (activityOpt.isPresent() && userOpt.isPresent()) {
            Review review = new Review();
            review.setActivity(activityOpt.get());
            review.setUser(userOpt.get());
            review.setStarsValue(starsValue);
            review.setDescription(description);

            reviewRepository.save(review);
        } else {
            throw new RuntimeException("Actividad o usuario no encontrado");
        }
    }

    public Page<Review> getReviewsPaginated(Long activityId, int page) {
        int size = 2; // Número de reviews por página
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findByActivityId(activityId, pageable); // Asegúrate de que el repositorio filtre por activityId
    }
    
    

}
