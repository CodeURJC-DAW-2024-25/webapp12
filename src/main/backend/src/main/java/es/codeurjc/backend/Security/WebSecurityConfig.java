package es.codeurjc.backend.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	public RepositoryUserDetailsService userDetailService;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() { //DIAPOSITIVA 20 SEGURIDAD 3.2 -- El componente creado con authenticationProvider() configura un autenticador con el servicio de usuarios en memoria y el password encoder creados anteriormente
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {		
		http.authenticationProvider(authenticationProvider());
		http.authorizeHttpRequests(authorize -> authorize
				// STATIC RESOURCES
				.requestMatchers("/assets/**", "/css/**", "/js/**", "/img/**", "/images/**", "/scss/**", "/cdn-cgi/**", "/cloudflare-static/**", "/plugins/**").permitAll()

				// PUBLIC PAGES
				.requestMatchers("/").permitAll()
				.requestMatchers("/login").permitAll()
				.requestMatchers("/signup").permitAll()
				.requestMatchers("/register").permitAll()
				.requestMatchers("/activity/**").permitAll()
				.requestMatchers("/index").permitAll()
				.requestMatchers("/moreActivities").permitAll()
				.requestMatchers("/moreReviews").permitAll()
				.requestMatchers("/search_page").permitAll()
				.requestMatchers("/activity/{id}/image").permitAll()
				.requestMatchers("/user/{id}/image").permitAll()
				.requestMatchers("/404").permitAll()
				
				
				
				// USER PAGES
				
				.requestMatchers("/profile").hasAnyRole("USER")
				.requestMatchers("/Edit_user-profile/**").hasAnyRole("USER")
				.requestMatchers("/activity/{activityId}/addReview").hasAnyRole("USER")
				// ADMIN PAGES
				.requestMatchers("/admin_activities").hasAnyRole("ADMIN")
				.requestMatchers("/admin_users").hasAnyRole("ADMIN")
				.requestMatchers("/statistics").hasAnyRole("ADMIN")
				.requestMatchers("/createActivity").hasAnyRole("ADMIN")
				.requestMatchers("/editActivity/**").hasAnyRole("ADMIN")
				.requestMatchers("/removeActivity").hasAnyRole("ADMIN")
				.requestMatchers("/removeUser").hasAnyRole("ADMIN")
				
				.anyRequest().authenticated())
		
				// LOGIN
				.formLogin(formLogin -> formLogin
					.loginPage("/login")
					.failureUrl("/loginError")
					.defaultSuccessUrl("/")
					.permitAll())
				// LOGOUT
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll());
		
		return http.build();
	}

}
