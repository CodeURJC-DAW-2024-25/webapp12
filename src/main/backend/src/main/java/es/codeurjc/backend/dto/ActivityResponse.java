package es.codeurjc.backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.codeurjc.backend.model.Activity;
import es.codeurjc.backend.model.Review;

public class ActivityResponse {
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("reviews")
    private List<Review> reviews;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("description")
    private String description;

    public ActivityResponse(Activity activity) {
        this.id = activity.getId();
        this.reviews = activity.getReviews();
        this.name = activity.getName();
        this.category = activity.getCategory();
        this.description = activity.getDescription();
    }

    // GETTERS AND SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}