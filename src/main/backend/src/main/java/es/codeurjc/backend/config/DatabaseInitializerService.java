package es.codeurjc.backend.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;


import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.backend.Model.Activity;

import es.codeurjc.backend.Repository.ActivityRepository;
import es.codeurjc.backend.Repository.PlaceRepository;
import es.codeurjc.backend.Repository.ReviewRepository;
import es.codeurjc.backend.Repository.UserRepository;

import es.codeurjc.backend.Model.User;
import es.codeurjc.backend.Model.Place;
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

	@Autowired
	private PlaceRepository placeRepository;

	
	@Autowired
	private PasswordEncoder passwordEncoder;


	
	@PostConstruct
	public void init() throws IOException, URISyntaxException {
		// Sample users
		User user1 = new User("Paula", "Ruiz Rubio", "paula@email.com", "12345567D", "556673336",passwordEncoder.encode("1234"));
		user1.setRoles(List.of("USER"));
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/images/user/user4.png")) { 

			byte[] imageData = inputStream.readAllBytes();
			SerialBlob imageBlob = new SerialBlob(imageData);
			user1.setImageFile(imageBlob);
			user1.setImage("user1.jpg");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		userRepository.save(user1);

		User user2 = new User("Alba", "Velasco Marqués", "alba@email.com", "12345567D", "556673336",passwordEncoder.encode("2345"));
		user2.setRoles(List.of("USER"));
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/images/user/user2.png")) { 

			byte[] imageData = inputStream.readAllBytes();
			SerialBlob imageBlob = new SerialBlob(imageData);
			user2.setImageFile(imageBlob);
			user2.setImage("user2.jpg");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		userRepository.save(user2);

		User user3 = new User("Alexandra", "Cararus Verdes", "alexandra@email.com", "12345567D", "556673336",passwordEncoder.encode("3456"));
		user3.setRoles(List.of("USER"));
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/images/user/user3.png")) { 

			byte[] imageData = inputStream.readAllBytes();
			SerialBlob imageBlob = new SerialBlob(imageData);
			user3.setImageFile(imageBlob);
			user3.setImage("user3.jpg");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		userRepository.save(user3);

		User user4 = new User("Gonzalo", "Perez Roca", "gonzalo@email.com", "12345567D","556673336",passwordEncoder.encode("4567"));
		user4.setRoles(List.of("USER"));
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/images/user/user5.png")) { 

			byte[] imageData = inputStream.readAllBytes();
			SerialBlob imageBlob = new SerialBlob(imageData);
			user4.setImageFile(imageBlob);
			user4.setImage("user4.jpg");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		userRepository.save(user4);

		User user5 = new User("Adriana", "Lopez", "adriana@email.com", "12345567D","123573336","8910");
		user5.setRoles(List.of("USER"));
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/images/user/user1.png")) { 

			byte[] imageData = inputStream.readAllBytes();
			SerialBlob imageBlob = new SerialBlob(imageData);
			user5.setImageFile(imageBlob);
			user5.setImage("user5.jpg");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		userRepository.save(user5);


		User user6 = new User("Paco", "Rodriguez", "paco@email.com","12345567D", "557930394","1567");
		user6.setRoles(List.of("USER"));
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/images/user/user5.png")) { 

			byte[] imageData = inputStream.readAllBytes();
			SerialBlob imageBlob = new SerialBlob(imageData);
			user6.setImageFile(imageBlob);
			user6.setImage("user6.jpg");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		userRepository.save(user6);



		User admin = new User("admin", "5678", "admin@email.com", "12345567D","556659504",passwordEncoder.encode("5678"));
		admin.setRoles(List.of("USER", "ADMIN"));
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/img/adminfoto.png")) { 

			byte[] imageData = inputStream.readAllBytes();
			SerialBlob imageBlob = new SerialBlob(imageData);
			admin.setImageFile(imageBlob);
			admin.setImage("admin_image.jpg");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		
		userRepository.save(admin);


		//SAMPLES DE LUGAR
		Place lugar1 = new Place();
		lugar1.setDescription("Este lugar contiene una piscina en la que se puede nadar. La piscina es una piscina olímpica de 50 metros de largo, donde podrás hacer mucho ejercicio");
		lugar1.setName("Piscina cubierta");
		placeRepository.save(lugar1); // Guardar en BD antes de asignarlo a actividades
		
		Place lugar2 = new Place();
		lugar2.setDescription("Se trata de pistas de atletismo que cuentan con pistas de rugby, fútbol, atletismo");
		lugar2.setName("Pistas exteriores");
		placeRepository.save(lugar2); // Guardar en BD antes de asignarlo a actividades
		
		Place lugar3 = new Place();
		lugar3.setDescription("Pista de bolos para jugar a los bolos que cuenta con 7 pistas de bolos");
		lugar3.setName("Pista de bolos");
		placeRepository.save(lugar3); 



		

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
		Activity activity1 = new Activity("Baloncesto", "Deporte equipo","Baloncesto para la wii",10);
		activity1.setReviews(List.of(review1, review6, review7));
		try (InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream("static/images/sports/baloncesto.png")) {
			byte[] imageData = inputStream.readAllBytes(); 
			SerialBlob imageBlob = new SerialBlob(imageData);
			activity1.setImageFile(imageBlob);
			activity1.setImageString("activity1.jpg");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		activity1.setPlace(lugar1);
		activity1.setActivityDate(Date.valueOf("2026-03-01"));
		
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
		

		Activity activity2 = new Activity("Beisbol", "Deporte bate", "Beisbol para la wii",4);
		activity2.setReviews(List.of(review2, review8));
		try (InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream("static/images/sports/beisbol.png")) {
			byte[] imageData = inputStream.readAllBytes(); 
			SerialBlob imageBlob = new SerialBlob(imageData);
			activity2.setImageFile(imageBlob);
			activity2.setImageString("activity2.jpg");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}

		activity2.setPlace(lugar1);
		activity2.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity2);
		review2.setActivity(activity2);
		review2.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review2);
		review8.setActivity(activity2);
		review8.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review8);

		Activity activity3 = new Activity("Bolos", "Deporte individual","Bolossss como molan los bolos (mentira)",1);
		activity3.setReviews(List.of(review3));
		try (InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream("static/images/sports/bolos.png")) {
			byte[] imageData = inputStream.readAllBytes(); 
			SerialBlob imageBlob = new SerialBlob(imageData);
			activity3.setImageFile(imageBlob);
			activity3.setImageString("activity3.jpg");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}

		activity3.setPlace(lugar1);
		activity3.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity3);
		review3.setActivity(activity3);
		review3.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review3);

		Activity activity4 = new Activity("Surf", "deporte de agua","sigue nadando sigue nadando",10);
		try (InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream("static/images/sports/surf.png")) {
			byte[] imageData = inputStream.readAllBytes(); 
			SerialBlob imageBlob = new SerialBlob(imageData);
			activity4.setImageFile(imageBlob);
			activity4.setImageString("activity4.jpg");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}

		activity4.setPlace(lugar1);
		activity4.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity4);
		review4.setActivity(activity4);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);

		Activity activity5 = new Activity("natacion", "deporte de agua","sigue nadando sigue nadando",10);
		
		activity5.setPlace(lugar1);
		activity5.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity5);
		review4.setActivity(activity5);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);

		Activity activity6 = new Activity("natacion", "deporte de agua","sigue nadando sigue nadando",10);
		
		activity6.setPlace(lugar1);
		activity6.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity6);
		review4.setActivity(activity6);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);
		
		Activity activity7 = new Activity("natacion", "deporte de agua","sigue nadando sigue nadando",10);
		
		activity7.setPlace(lugar2);
		activity7.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity7);
		review4.setActivity(activity7);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);
		
		Activity activity8 = new Activity("natacion", "deporte de agua","sigue nadando sigue nadando",10);
		
		activity8.setPlace(lugar2);
		activity8.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity8);
		review4.setActivity(activity8);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);
		
		Activity activity9 = new Activity("natacion", "deporte de agua","sigue nadando sigue nadando",10);
		
		activity9.setPlace(lugar2);
		activity9.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity9);
		review4.setActivity(activity9);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);
		
		Activity activity10 = new Activity("natacion", "deporte de agua","sigue nadando sigue nadando",10);
		
		activity10.setPlace(lugar3);
		activity10.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity10);
		review4.setActivity(activity10);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);

		Activity activity11 = new Activity("natacion", "deporte de agua","sigue nadando sigue nadando",10);
		
		activity11.setPlace(lugar3);
		activity11.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity11);
		review4.setActivity(activity11);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);

		Activity activity12 = new Activity("natacion", "deporte de agua","sigue nadando sigue nadando",10);
		
		activity12.setPlace(lugar3);
		activity12.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity12);
		review4.setActivity(activity12);
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

		user5.setActivities(List.of(activity1, activity2, activity3, activity4,activity5));
		userRepository.save(user5);

		user6.setActivities(List.of(activity1, activity2, activity3, activity4,activity5,activity6));
		userRepository.save(user6);

	}

}