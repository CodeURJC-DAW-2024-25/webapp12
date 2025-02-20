package es.codeurjc.backend.config;

import java.io.IOException;

import java.net.URISyntaxException;

import java.util.Calendar;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import es.codeurjc.backend.Model.Activity;

import es.codeurjc.backend.Repository.ActivityRepository;
import es.codeurjc.backend.Repository.ReviewRepository;
import es.codeurjc.backend.Repository.UserRepository;

import es.codeurjc.backend.Model.User;
import es.codeurjc.backend.Model.Review;
import jakarta.annotation.PostConstruct;

@Service
public class DatabaseInitializerService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private ActivityRepository activityRepository;

	
	
	private String adminMail;

	
	private String encodedPassword;

	@PostConstruct
	public void init() throws IOException, URISyntaxException {
		// Sample users
		User user1 = new User("Paula", "Ruiz Rubio", "paula@email.com", "1234");
		user1.setRoles(List.of("USER"));
		userRepository.save(user1);

		User user2 = new User("Alba", "Velasco Marqués", "alba@email.com", "2345");
		user2.setRoles(List.of("USER"));
		userRepository.save(user2);

		User user3 = new User("Alexandra", "Cararus Verdes", "alexandra@email.com", "3456");
		user3.setRoles(List.of("USER"));
		userRepository.save(user3);

		User user4 = new User("Gonzalo", "Perez Roca", "gonzalo@email.com", "4567");
		user4.setRoles(List.of("USER"));
		userRepository.save(user4);


		User admin = new User("admin", "5678", adminMail, encodedPassword);
		admin.setRoles(List.of("USER", "ADMIN"));
		
		userRepository.save(admin);

		// Sample reviews
		Review review1 = new Review(1, "No me ha gustado nada, muy mejorable :(", user1);
		Review review2 = new Review(2, "He aprendido poco, es muy para principiantes", user2);
		Review review3 = new Review(3, "Me ha servido, aunque hay cosas que he tenido que buscar en internet", user3);
		Review review4 = new Review(4,"Es un curso fantastico, aunque podrían poner los comandos en un archivo adjunto", user4);		
		//Review review5 = new Review(5, "Perfecto, gracias a este curso soy mucho mejor profesional", user3);
		Review review6 = new Review(3, "No he encontrado útiles las explicaciones, demasiado confusas", user1);
		Review review7 = new Review(4, "Buena introducción al tema, pero necesitaría más ejercicios prácticos", user4);
		Review review8 = new Review(5, "Increíble, he aprendido tanto en tan poco tiempo, gracias al equipo del curso",user2);
		

		// Sample activitys
		Activity activity1 = new Activity("Padel", "Deporte equipo","Padel para la wii");
		activity1.setReviews(List.of(review1, review6, review7));
		
		activityRepository.save(activity1);
		review1.setActivity(activity1);
		review1.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review1);
		review6.setActivity(activity1);
		review6.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review6);
		review7.setActivity(activity1);
		review7.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review7);
		

		Activity activity2 = new Activity("Tenis", "Deporte raqueta", "Tenis para la wii");
		activity2.setReviews(List.of(review2, review8));
		activityRepository.save(activity2);
		review2.setActivity(activity2);
		review2.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review2);
		review8.setActivity(activity2);
		review8.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review8);

		Activity activity3 = new Activity("Correr", "Deporte individual","Corre corre que te corre");
		activity3.setReviews(List.of(review3));
		activityRepository.save(activity3);
		review3.setActivity(activity3);
		review3.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review3);

		Activity activity4 = new Activity("natacion", "deporte de agua","sigue nadando sigue nadando");
		
		activityRepository.save(activity4);
		review4.setActivity(activity4);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);
		

		

		user1.setActivities(List.of(activity1));
		userRepository.save(user1);

		user2.setActivities(List.of(activity1, activity2));
		userRepository.save(user2);

		user3.setActivities(List.of(activity1, activity2, activity3));
		userRepository.save(user3);

		user4.setActivities(List.of(activity1, activity2, activity3, activity4));
		userRepository.save(user4);

		

	}

}