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
				.requestMatchers("/activity/**").permitAll()

				
				// USER PAGES
				.requestMatchers("/#").hasAnyRole("USER")

				// ADMIN PAGES
				.requestMatchers("/#").hasAnyRole("ADMIN")
				
				.anyRequest().authenticated())
		
				// LOGIN
				.formLogin(formLogin -> formLogin
					.loginPage("/login")
					.failureUrl("/loginError")
					.defaultSuccessUrl("/", true)
					.permitAll());
				// LOGOUT
				/*.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll()); */
		
		return http.build();
	}

}
