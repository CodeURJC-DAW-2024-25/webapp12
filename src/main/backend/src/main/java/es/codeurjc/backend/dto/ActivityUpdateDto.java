package es.codeurjc.backend.dto;

import java.util.Date;
public class ActivityUpdateDto {
    private String name;
    private String category;
    private String description;
    private Boolean imageBoolean;
    private Integer vacancy;
    private Date activityDate;
    private Long placeId;

    // Getters y setters
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

    public Boolean getImageBoolean() {
        return imageBoolean;
    }

    public void setImageBoolean(Boolean imageBoolean) {
        this.imageBoolean = imageBoolean;
    }

    public Integer getVacancy() {
        return vacancy;
    }

    public void setVacancy(Integer vacancy) {
        this.vacancy = vacancy;
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }
}