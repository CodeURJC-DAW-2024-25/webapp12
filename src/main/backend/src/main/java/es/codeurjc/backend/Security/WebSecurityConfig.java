package es.codeurjc.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.http.HttpMethod;
 import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
 
 import es.codeurjc.backend.security.jwt.JwtRequestFilter;
 import es.codeurjc.backend.security.jwt.UnauthorizedHandlerJwt;






@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	public RepositoryUserDetailsService userDetailService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}


	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http
			.securityMatcher("/api/**")
			.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));
		
		http
			.authorizeHttpRequests(authorize -> authorize
                    // PRIVATE ENDPOINTS
                    
					
					
					.requestMatchers(HttpMethod.GET,"/api/activities/").permitAll()
					.requestMatchers(HttpMethod.GET,"/api/activities/").permitAll()
					.requestMatchers(HttpMethod.GET,"/api/activities/{id}").permitAll()
					.requestMatchers(HttpMethod.GET,"/api/activities/{id}/image").permitAll()
					.requestMatchers(HttpMethod.GET,"/api/activities/user/{id}").permitAll()
					.requestMatchers(HttpMethod.GET,"/api/activities/search").permitAll()
					.requestMatchers(HttpMethod.GET,"/api/reviews/activity/").permitAll()
					


					.requestMatchers(HttpMethod.POST,"/api/activities/{id}/reserve").hasRole("USER")
					.requestMatchers(HttpMethod.POST,"/api/activities/users/").hasRole("USER")
					.requestMatchers(HttpMethod.POST,"/api/activities/user/").hasRole("USER")
					.requestMatchers(HttpMethod.POST,"/api/reviews/activity/").hasRole("USER")
					.requestMatchers(HttpMethod.PUT,"/api/reviews/").hasRole("USER")

					.requestMatchers(HttpMethod.POST,"/api/activities/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET,"/api/users/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.PUT,"/api/activities/{id}").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET,"/api/users/{id}").hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE,"/api/activities/{id}").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET,"/api/users/{id}/image").hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE,"/api/activities/{id}/image").hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE,"/api/users/{id}").hasRole("ADMIN")
					.requestMatchers(HttpMethod.POST,"/api/users/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.PUT,"/api/users/{id}").hasRole("ADMIN")
					
					.requestMatchers(HttpMethod.POST,"/api/activities/{id}/image").hasRole("ADMIN")
					.requestMatchers(HttpMethod.PUT,"/api/activities/{id}/image").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET,"/api/statistics/activities-by-month").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET,"/api/statistics/review-statistics").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET,"/api/statistics/general-statistics").hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE,"/api/reviews/").hasRole("ADMIN")


					// PUBLIC ENDPOINTS
					.anyRequest().permitAll()
			);
		
        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Add JWT Token filter
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}


	@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            "/assets/**", "/css/**", "/js/**", "/img/**", "/images/**", 
            "/scss/**", "/cdn-cgi/**", "/cloudflare-static/**", "/plugins/**"
        );}

	@Bean
	@Order(1)
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {		
		http.authenticationProvider(authenticationProvider());
		http.authorizeHttpRequests(authorize -> authorize
				// STATIC RESOURCES
				.requestMatchers("/assets/", "/css/", "/js/", "/img/", "/images/", "/scss/", "/cdn-cgi/", "/cloudflare-static/", "/plugins/").permitAll()

				// PUBLIC PAGES
				.requestMatchers("/").permitAll()
				.requestMatchers("/login").permitAll()
				.requestMatchers("/signup").permitAll()
				.requestMatchers("/register").permitAll()
				.requestMatchers("/activity/").permitAll()
				.requestMatchers("/index").permitAll()
				.requestMatchers("/moreActivities").permitAll()
				.requestMatchers("/moreReviews").permitAll()
				.requestMatchers("/searchPage").permitAll()
				.requestMatchers("/activity/{id}/image").permitAll()
				.requestMatchers("/user/{id}/image").permitAll()
				.requestMatchers("/404").permitAll()
				
				
				
				// USER PAGES
				
				.requestMatchers("/profile").hasAnyRole("USER")
				.requestMatchers("/editUserProfile/").hasAnyRole("USER")
				.requestMatchers("/activity/{activityId}/addReview").hasAnyRole("USER")
				// ADMIN PAGES
				.requestMatchers("/adminActivities").hasAnyRole("ADMIN")
				.requestMatchers("/adminUsers").hasAnyRole("ADMIN")
				.requestMatchers("/statistics").hasAnyRole("ADMIN")
				.requestMatchers("/createActivity").hasAnyRole("ADMIN")
				.requestMatchers("/editActivity/").hasAnyRole("ADMIN")
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