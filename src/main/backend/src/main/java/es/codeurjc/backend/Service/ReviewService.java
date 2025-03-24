package es.codeurjc.backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.backend.dto.ReviewDto;
import es.codeurjc.backend.model.Activity;
import es.codeurjc.backend.model.Review;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.repository.ActivityRepository;
import es.codeurjc.backend.repository.ReviewRepository;
import es.codeurjc.backend.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class ReviewService {
     @Autowired
    private ReviewRepository reviewRepository; 

     @Autowired
    private ActivityRepository activityRepository; 

    @Autowired
    private UserRepository userRepository;
    
    public void delete(long id){
        reviewRepository.deleteById(id);
    }

    public List<Review> findByActivity_Id(Long id) {
        return reviewRepository.findByActivity_Id(id);

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
        Optional<Activity> activityOpt = activityRepository.findById(activityId);
        Optional<User> userOpt = userRepository.findById(userId); 

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
        int size = 2; 
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findByActivityId(activityId, pageable); 
    }

    public Map<Integer, Long> countReviewsByValorationDto() {
        return reviewRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Review::getStarsValue, 
                        Collectors.counting()  
                ));
    }

    @Transactional
    public Page<ReviewDto> getReviewsPaginatedDto(Long activityId, int page) {
        int size = 2;
        Pageable pageable = PageRequest.of(page, size);

        Page<Review> reviewsPage = reviewRepository.findByActivityId(activityId, pageable);

        return reviewsPage.map(review -> new ReviewDto(
            review.getId(),
            review.getDescription(),
            review.getStarsValue(),
            review.getCreationDate(),
            review.getUserFullName()
        ));
    }

    @Transactional
    public ReviewDto saveReviewDto(Long activityId, int starsValue, String comment, Long userId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        Review review = new Review();
        review.setStarsValue(starsValue);
        review.setDescription(comment);
        review.setUser(user);
        review.setActivity(activity);
        review.setCreationDate(Calendar.getInstance());
    
        Review savedReview = reviewRepository.save(review);
    
        return new ReviewDto(
                savedReview.getId(),
                savedReview.getDescription(),
                savedReview.getStarsValue(),
                savedReview.getCreationDate(),
                savedReview.getUserFullName() 
        );
    }

    @Transactional
    public ReviewDto updateReview(Long reviewId, int starsValue, String comment, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Revisión no encontrada"));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para modificar esta revisión");
        }

        review.setStarsValue(starsValue);
        review.setDescription(comment);

        Review updatedReview = reviewRepository.save(review);

        return new ReviewDto(
                updatedReview.getId(),
                updatedReview.getDescription(),
                updatedReview.getStarsValue(),
                updatedReview.getCreationDate(),
                updatedReview.getUserFullName()
        );
    }

    @Transactional
    public void deleteReviewById(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Revisión no encontrada");
        }
    
        reviewRepository.deleteById(reviewId);
    }
        
    

}