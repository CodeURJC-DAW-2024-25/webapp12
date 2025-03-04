package es.codeurjc.backend.config;

import java.io.IOException;

import java.net.URISyntaxException;
import java.sql.Date;

import java.util.Calendar;
import java.util.List;




import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.core.io.Resource;

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
		setUserImage(user1, "static/images/user/user4.png");
		
		userRepository.save(user1);

		User user2 = new User("Alba", "Velasco Marqués", "alba@email.com", "12345567D", "556673336",passwordEncoder.encode("2345"));
		user2.setRoles(List.of("USER"));
		setUserImage(user2, "static/images/user/user2.png");
		userRepository.save(user2);

		User user3 = new User("Alexandra", "Cararus Verdes", "alexandra@email.com", "12345567D", "556673336",passwordEncoder.encode("3456"));
		user3.setRoles(List.of("USER"));
		setUserImage(user3, "static/images/user/user3.png");
		userRepository.save(user3);

		User user4 = new User("Gonzalo", "Perez Roca", "gonzalo@email.com", "12345567D","556673336",passwordEncoder.encode("4567"));
		user4.setRoles(List.of("USER"));
		setUserImage(user4, "static/images/user/user5.png");
	
		userRepository.save(user4);

		User user5 = new User("Adriana", "Lopez", "adriana@email.com", "12345567D","123573336","8910");
		user5.setRoles(List.of("USER"));
		setUserImage(user5, "static/images/user/user1.png");
		
		userRepository.save(user5);


		User user6 = new User("Paco", "Rodriguez", "paco@email.com","12345567D", "557930394","1567");
		user6.setRoles(List.of("USER"));
		setUserImage(user6, "static/images/user/user5.png");
		
		userRepository.save(user6);

		User admin = new User("admin", "5678", "admin@email.com", "12345567D","556659504",passwordEncoder.encode("5678"));
		admin.setRoles(List.of("USER", "ADMIN"));
		setUserImage(admin, "static/img/adminfoto.png");
		
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

		Place lugar4 = new Place();
		lugar4.setDescription("Pistas cubiertas para jugar al baloncesto, padel, tennis y un centro de bateo");
		lugar4.setName("Pistas cubiertas");
		placeRepository.save(lugar4); 

		Place lugar5 = new Place();
		lugar5.setDescription("Pequeña playa privada reservada a las actividades del resort");
		lugar5.setName("Playa del Resort");
		placeRepository.save(lugar5); 

		Place lugar6 = new Place();
		lugar6.setDescription("Pista de golf con 18 hoyos y un campo de prácticas");
		lugar6.setName("Pista de golf");
		placeRepository.save(lugar6);

		Place lugar7 = new Place();
		lugar7.setDescription("Montaña con diferentes senderos y rutas por el bosque");
		lugar7.setName("Rutas de montaña");
		placeRepository.save(lugar7);

		Place lugar8 = new Place();
		lugar8.setDescription("Salas de gimnasio indoor para hacer Danza,Yoga o Pilates");
		lugar8.setName("Salas Indoor");
		placeRepository.save(lugar8);

		



		

		// Sample reviews
		Review review1 = new Review(1, "No me ha gustado nada, muy mejorable :(", user1);
		Review review2 = new Review(2, "He aprendido poco, es muy para principiantes", user2);
		Review review3 = new Review(3, "Me ha servido, aunque hay cosas que he tenido que buscar en internet", user3);
		Review review4 = new Review(4,"Es un curso fantastico, aunque podrían poner los comandos en un archivo adjunto", user4);		
		Review review5 = new Review(5, "Perfecto, gracias a este curso soy mucho mejor profesional", user3);
		Review review6 = new Review(3, "No he encontrado útiles las explicaciones, demasiado confusas", user1);
		Review review7 = new Review(4, "Buena introducción al tema, pero necesitaría más ejercicios prácticos", user4);
		Review review8 = new Review(5, "Increíble, he aprendido tanto en tan poco tiempo, gracias al equipo del curso",user2);
		

		// Sample activitys
		Activity activity1 = new Activity("Baloncesto", "Deporte de equipo","Disfruta de una emocionante competencia en nuestra cancha de baloncesto. Forma tu equipo, compite en partidos amistosos y demuestra tus habilidades en un ambiente divertido y relajado. Ideal para jugadores de todos los niveles, esta actividad es perfecta para ejercitarte, socializar y vivir la pasión del baloncesto mientras disfrutas del sol y la brisa del resort. ¡Inscríbete y únete a la diversión!",10);
		activity1.setReviews(List.of(review1, review6, review7));
		setActivityImage(activity1, "static/images/sports/baloncesto.png");
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
		review5.setActivity(activity1);
		review5.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review5);
		

		Activity activity2 = new Activity("Beisbol", "Deporte de equipo", "Vive la emoción del béisbol en un entorno espectacular. Únete a un equipo, batea, corre las bases y atrapa la victoria en un partido amistoso diseñado para todas las edades y niveles de experiencia. Disfruta del espíritu deportivo, la competencia sana y la diversión bajo el sol del resort. ¡Inscríbete y sé parte del juego!",4);
		activity2.setReviews(List.of(review2, review8));
		setActivityImage(activity2, "static/images/sports/beisbol.png");
		activity2.setPlace(lugar1);
		activity2.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity2);
		review2.setActivity(activity2);
		review2.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review2);
		review8.setActivity(activity2);
		review8.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review8);

		Activity activity3 = new Activity("Bolos", "Deportes individuales","Disfruta de una divertida partida de bolos en un ambiente relajado y amigable. Perfecto para todas las edades, este juego combina habilidad y entretenimiento mientras compartes risas y buenos momentos con amigos y familia. ¡Apunta, lanza y derriba todos los pinos para convertirte en el campeón de la noche!",10);
		activity3.setReviews(List.of(review3));
		setActivityImage(activity3, "static/images/sports/bolos.png");
		
		activity3.setPlace(lugar1);
		activity3.setActivityDate(Date.valueOf("2026-04-01"));

		activityRepository.save(activity3);
		review3.setActivity(activity3);
		review3.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review3);

		Activity activity4 = new Activity("Surf", "deporte de agua","sigue nadando sigue nadando",10);
		setActivityImage(activity4, "static/images/sports/surf.png");

		activity4.setPlace(lugar5);
		activity4.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity4);
		review4.setActivity(activity4);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);

		Activity activity5 = new Activity("Natacion", "Deportes individuales","Refresca tu día y mejora tus habilidades en el agua con nuestras clases de natación, diseñadas para todas las edades y niveles. Aprende técnicas de respiración, flotación y estilos de nado con instructores certificados, todo en un ambiente seguro y relajante. ¡Sumérgete en la diversión y disfruta del agua como nunca!",20);
		
		setActivityImage(activity5, "static/images/sports/natacion.png");
		activity5.setPlace(lugar1);
		activity5.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity5);
		review4.setActivity(activity5);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);

		Activity activity6 = new Activity("Rugby", "Deporte de contacto","Siente la emoción y la intensidad del rugby en un partido amistoso diseñado para todos los niveles. Forma tu equipo, corre, pasa y anota mientras disfrutas de la competencia en un ambiente dinámico y lleno de energía. ¡Únete a la acción y vive la pasión del rugby en el resort!",22);
		
		activity6.setPlace(lugar2);
		setActivityImage(activity6, "static/images/sports/rugby.png");
		activity6.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity6);
		review4.setActivity(activity6);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);
		
		Activity activity7 = new Activity("Atletismo", "Deporte individual","Ponte a prueba y libera tu energía con nuestras actividades de atletismo. Desde carreras de velocidad hasta resistencia y saltos, disfruta de un entrenamiento dinámico en un entorno espectacular. Ideal para todos los niveles, esta es la oportunidad perfecta para mejorar tu rendimiento y divertirte al aire libre. ¡Acepta el desafío y cruza la meta con nosotros!",10);
		
		setActivityImage(activity7, "static/images/sports/atletismo.png");
		activity7.setPlace(lugar2);
		activity7.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity7);
		review4.setActivity(activity7);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);
		
		Activity activity8 = new Activity("Padel", "Deporte de euipo","Disfruta de la emoción del pádel en nuestras modernas canchas, perfectas para jugadores de todos los niveles. Forma pareja, mejora tu técnica y compite en partidos dinámicos llenos de estrategia y diversión. Una excelente manera de hacer ejercicio, socializar y disfrutar del deporte en un entorno único. ¡Reserva tu turno y vive la pasión del pádel!",10);
		
		activity8.setPlace(lugar4);
		setActivityImage(activity8, "static/images/sports/tennis.png");
		activity8.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity8);
		review4.setActivity(activity8);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);
		
		Activity activity9 = new Activity("Tennis", "Deporte individual","Vive la emoción del tenis en nuestras canchas de primer nivel, diseñadas para jugadores de todos los niveles. Ya sea que quieras practicar tu saque, mejorar tu juego en pareja o competir en un partido amistoso, esta actividad es perfecta para disfrutar de un deporte dinámico y entretenido. ¡Ven a desafiarte a ti mismo y a disfrutar de un buen partido de tenis!",10);
		
		activity9.setPlace(lugar4);
		activity9.setActivityDate(Date.valueOf("2026-04-15"));
		setActivityImage(activity9, "static/images/sports/tennis.png");
		activity9.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity9);
		review4.setActivity(activity9);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);
		
		Activity activity10 = new Activity("Tiro con Arco", "Deporte individual","sPon a prueba tu precisión y concentración con nuestra actividad de tiro con arco. Guiado por instructores experimentados, aprenderás las técnicas básicas para lanzar con destreza y alcanzar el blanco. Ideal para todos los niveles, esta actividad te permitirá disfrutar de un desafío relajante en un entorno natural. ¡Apunta, dispara y alcanza tu objetivo!",15);
		
		activity10.setPlace(lugar2);
		activity10.setActivityDate(Date.valueOf("2025-07-01"));
		setActivityImage(activity10, "static/images/sports/arco.png");
		
		activity10.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity10);
		review4.setActivity(activity10);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);

		Activity activity11 = new Activity("golf", "Deporte individual","Disfruta de un día de golf en nuestro hermoso campo, rodeado de vistas impresionantes. Ya sea que seas principiante o un golfista experimentado, podrás mejorar tu técnica y disfrutar de un juego relajado o competitivo. Perfecto para compartir con amigos o disfrutar en solitario mientras te conectas con la naturaleza. ¡Ven a disfrutar del golf y haz de cada golpe una experiencia única!",7);
		
		activity11.setPlace(lugar6);
		activity11.setActivityDate(Date.valueOf("2026-01-01"));
		setActivityImage(activity11, "static/images/sports/golf.png");
		
		activity11.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity11);
		review4.setActivity(activity11);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);

		Activity activity12 = new Activity("Paseo por la montaña", "Relajacion","Conéctate con la naturaleza en un recorrido guiado por los hermosos senderos de montaña. Disfruta de vistas panorámicas, aire fresco y la tranquilidad del paisaje mientras caminas a tu propio ritmo. Perfecto para los amantes del senderismo, este paseo es una excelente oportunidad para relajarte, explorar y admirar la belleza natural que rodea el resort. ¡Ponte tus botas y ven a descubrir lo que la montaña tiene para ofrecer!",20);
		
		activity12.setPlace(lugar7);
		activity12.setActivityDate(Date.valueOf("2026-01-01"));
		setActivityImage(activity12, "static/images/sports/montaña.png");
		activity12.setActivityDate(Date.valueOf("2026-03-01"));

		activityRepository.save(activity12);
		review4.setActivity(activity12);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);
		

		Activity activity13 = new Activity("Pilates", "Relajacion","Fortalece tu cuerpo y mente con nuestras clases de pilates, diseñadas para mejorar tu flexibilidad, postura y equilibrio. Guiadas por instructores certificados, estas sesiones ofrecen un enfoque relajante y tonificante, adaptado a todos los niveles. Perfecto para quienes buscan una actividad de bajo impacto que promueva el bienestar general. ¡Únete y siente los beneficios de Pilates en un entorno tranquilo y relajante!",20);
		
		activity13.setPlace(lugar8);
		activity13.setActivityDate(Date.valueOf("2025-05-09"));
		setActivityImage(activity13, "static/images/sports/yoga.png");
		activityRepository.save(activity13);
		review4.setActivity(activity13);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);


		Activity activity14 = new Activity("Yoga", "Relajacion","Relájate, fortalece tu cuerpo y calma tu mente con nuestras clases de yoga, ideales para todos los niveles. En un entorno tranquilo y rodeado de naturaleza, practicarás posturas que mejoran tu flexibilidad, equilibrio y bienestar general. Ya sea para iniciarte o profundizar en tu práctica, nuestras clases de yoga ofrecen una experiencia revitalizante que te ayudará a encontrar tu centro. ¡Ven y disfruta de una conexión profunda entre cuerpo, mente y espíritu!",20);
		
		activity14.setPlace(lugar8);
		activity14.setActivityDate(Date.valueOf("2025-04-30"));
		setActivityImage(activity14, "static/images/sports/yoga.png");
		activityRepository.save(activity14);
		review4.setActivity(activity14);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);


		Activity activity15 = new Activity("Danza", "Relajacion","Exprésate y diviértete mientras aprendes nuevos pasos en nuestras clases de danza. Desde ritmos latinos hasta estilos modernos, estas clases están diseñadas para todos los niveles. Disfruta de la música, mejora tu coordinación y suelta tu energía en un ambiente alegre y lleno de ritmo. ¡No importa si eres principiante o ya tienes experiencia, ven a mover el cuerpo y a disfrutar de la danza!",20);
		
		activity15.setPlace(lugar8);
		activity15.setActivityDate(Date.valueOf("2025-09-10"));
		setActivityImage(activity15, "static/images/sports/baile.png");
		activityRepository.save(activity15);
		review4.setActivity(activity15);
		review4.setCreationDate(Calendar.getInstance());
		reviewRepository.save(review4);


	
		user1.setActivities(List.of(activity1,activity11,activity14));
		userRepository.save(user1);

		user2.setActivities(List.of(activity1, activity2,activity8));
		userRepository.save(user2);

		user3.setActivities(List.of(activity1, activity2, activity3,activity9));
		userRepository.save(user3);

		user4.setActivities(List.of(activity1, activity2, activity3, activity4));
		userRepository.save(user4);

		user5.setActivities(List.of(activity1, activity2, activity3, activity4,activity5,activity12));
		userRepository.save(user5);

		user6.setActivities(List.of(activity1, activity2, activity3, activity4,activity5,activity6,activity15));
		userRepository.save(user6);

	}

	public void setActivityImage(Activity activity, String classpathResource) throws IOException {
		activity.setImage(true);
		Resource image = new ClassPathResource(classpathResource);
		activity.setImageFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
	}

	public void setUserImage(User user, String classpathResource) throws IOException {
		user.setImage(true);
		Resource image = new ClassPathResource(classpathResource);
		user.setImageFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
	}

}