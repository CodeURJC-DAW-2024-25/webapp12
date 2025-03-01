package es.codeurjc.backend.Model;


import java.util.Calendar;
import java.util.List;
import java.sql.Blob;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;



import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Activity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@ManyToOne
	@JoinColumn(name = "place_id")
	private Place place;

	@OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Review> reviews;

	@ManyToMany (mappedBy = "activities")
	@JsonIgnore
	private List<User> users;


	private String name;
	private String category;
	private String description;
	private int vacancy;
	
	private Calendar creationDate;
	private String formattedCreationDate;

	private Date activityDate;


	

	@Lob
	@JsonIgnore
	private Blob imageFile;

	private String imageString;
	

	public Activity() {
		super();
	}

	public Activity(String name, String category, String description, int vacancy) {
		this.name = name;
		this.category = category;
		this.description = description;
		this.vacancy = vacancy;
		this.creationDate = Calendar.getInstance();
		this.creationDate.set(Calendar.HOUR_OF_DAY, 0);
		this.formattedCreationDate = new SimpleDateFormat("dd/MM/yyyy").format(this.creationDate.getTime());
	}


	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public String getName() {
		return name;
	}

	public void setName(String nombre) {
		this.name = nombre;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String categoria) {
		this.category = categoria;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String descripcion) {
		this.description = descripcion;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public int getVacancy(){
		return vacancy;
	}

	public void setVacancy(int vacancy){
		this.vacancy = vacancy;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}


	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public void setCreationDateMethod (){
		this.creationDate = Calendar.getInstance();
		this.creationDate.set(Calendar.HOUR_OF_DAY, 0);
		this.formattedCreationDate = new SimpleDateFormat("dd/MM/yyyy").format(this.creationDate.getTime());
	}

	public String getFormattedCreationDate() {
		return formattedCreationDate;
	}

	public void setFormattedCreationDate(String formattedCreationDate) {
		this.formattedCreationDate = formattedCreationDate;
	}
	public String getImageString() {
		return imageString;
	}

	public void setImageString(String imageString) {
		this.imageString = imageString;
	}	

	public Blob getImageFile() {
		return imageFile;
	}

	public void setImageFile(Blob imageFile) {
		this.imageFile = imageFile;
	}
	
	public Date getActivityDate() {
		return activityDate;
	}

	public Date setActivityDate(Date activityDate) {
		return this.activityDate = activityDate;
	}



	
}