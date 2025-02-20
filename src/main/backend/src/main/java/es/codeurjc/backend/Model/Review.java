package es.codeurjc.backend.Model;

import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private int starsValue;
	private String description;
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "activity_id")
	private Activity activity;
	@ManyToOne
	@JsonIgnore
	private User user;
	private Calendar creationDate;

	public Review() {
		super();
	}

	public Review(int starsValue, String description, User user) {
		super();
		this.starsValue = starsValue;
		this.description = description;
		this.user = user;
	}

	public Review(int starsValue, String description, Activity activity, User user) {
		super();
		this.starsValue = starsValue;
		this.description = description;
		this.activity = activity;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getStarsValue() {
		return starsValue;
	}

	public void setStarsValue(int starsValue) {
		this.starsValue = starsValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}
}