package es.codeurjc.backend.Model;


import java.sql.Blob;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class User {

	private String name;
	private String surname;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String password;
	private String phone;
	private String dni;
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;
	@ManyToMany (fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Activity> activities;
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Review> reviews;
	

	@Lob
	@JsonIgnore
	private Blob imageFile;

	private boolean imageBoolean;
	

	public User() {
		super();
	}

	public User(String name, String surname, String email,String dni,String phone, String password) {
		
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.dni = dni;
		this.phone = phone;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}


	public Blob getImageFile() {
		return imageFile;
	}

	public void setImageFile(Blob imageFile) {
		this.imageFile = imageFile;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public boolean getImage(){
		return this.imageBoolean;
	}

	public void setImage(boolean imageBoolean){
		this.imageBoolean = imageBoolean;
	}

}