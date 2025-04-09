package es.codeurjc.backend.auth;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.backend.dto.UserDto;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.security.jwt.AuthResponse;
import es.codeurjc.backend.security.jwt.AuthResponse.Status;
import es.codeurjc.backend.security.jwt.LoginRequest;
import es.codeurjc.backend.security.jwt.UserLoginService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
	
	@Autowired
	private UserLoginService userLoginService;


	@Operation(summary = "Login", description = "Authenticate user and generate access token.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
	})
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(
			@RequestBody LoginRequest loginRequest,
			HttpServletResponse response) {
		
		return userLoginService.login(response, loginRequest);
	}
	
	@Operation(summary = "Refresh Token", description = "Refreshes the access token using the refresh token.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request - Missing or invalid refresh token", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized - Invalid refresh token", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
	})
	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(
			@CookieValue(name = "RefreshToken", required = false) String refreshToken, 
			HttpServletResponse response) {
		
		if (refreshToken == null || refreshToken.isEmpty()) {
			return ResponseEntity.ok(new AuthResponse(AuthResponse.Status.FAILURE, "No refresh token provided"));
		}
		
		return userLoginService.refresh(response, refreshToken);
	}

	@Operation(summary = "Logout", description = "Logs out the current user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Logout successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
	})
	@PostMapping("/logout")
	public ResponseEntity<AuthResponse> logOut(HttpServletResponse response) {
		return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userLoginService.logout(response)));
	}

	@GetMapping(value = "/me")
	public UserDto getCurrentUser(Principal principal) {				
		String username = principal.getName();
		User user = userLoginService.findByEmail(username);		
		return userLoginService.getUserDto(user.getId());

	}
}