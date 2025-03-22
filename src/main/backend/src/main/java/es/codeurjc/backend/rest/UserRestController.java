package es.codeurjc.backend.rest;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.mapstruct.control.MappingControl.Use;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import es.codeurjc.backend.model.User;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.backend.controllers.UserController;
import es.codeurjc.backend.dto.ActivityDto;
import es.codeurjc.backend.dto.NewUserDto;
import es.codeurjc.backend.dto.UserDto;
import es.codeurjc.backend.dto.UserUpdateDto;
import es.codeurjc.backend.model.Activity;
import es.codeurjc.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;






@RestController
@RequestMapping("/api/users")
public class UserRestController {

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public Collection<UserDto> getUsers() {

		return userService.getUsersDtos();
	}
	@Operation(summary = "Get every users", description = "Returns a list with every user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List with users returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content) })
	
	@GetMapping("/pageable")
	public Page<UserDto> getUsers(@RequestParam(defaultValue = "0") int page,
								@RequestParam(defaultValue = "4") int size) { // Tamaño por defecto 4
		return userService.getAllUsersPaginated(page, size);
	}
	@Operation(summary = "Get user based on ID", description = "Returns the user whose ID matches the one on the URL.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content) })
	
	@GetMapping("/{id}")
	public UserDto getUser(@PathVariable Long id) {
		return userService.getUserDto(id);
	}
	@Operation(summary = "Get the user photo", description = "Returns the user image based on the ID")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Image returned successfully", content = @Content),
		@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
		@ApiResponse(responseCode = "404", description = "User not found", content = @Content)
	})
	@GetMapping("/{id}/image")
	public ResponseEntity<Object> getUserImage(@PathVariable Long id) throws SQLException,IOException {
		try {
            Resource postImage = userService.getUserImage(id);
            return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(postImage);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
	}

	@PostMapping("/")
	public ResponseEntity<UserDto> createUser(@RequestBody NewUserDto newUserDto){
        UserDto userDto = userService.createUser(newUserDto);

		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(userDto.id()).toUri();

		return ResponseEntity.created(location).body(userDto);
    } 
	@Operation(summary = "Delete user", description = "Users an activity and returns that deleted user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content),
			@ApiResponse(responseCode = "405", description = "Not allowed", content = @Content)
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<UserDto> deleteUser(@PathVariable Long id) {
		UserDto deletedUser = userService.deleteUser(id);
		return ResponseEntity.ok().body(deletedUser);
	}
	@Operation(summary = "Delete user image", description = "Deletes an user imageand returns that deleted user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User deleted successfully"),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content),
			@ApiResponse(responseCode = "405", description = "Not allowed", content = @Content)
	})
	@DeleteMapping("/{id}/image")
	public ResponseEntity<Object> deleteUserImage(@PathVariable Long id)throws IOException{
		userService.deleteUserImage(id);
		return ResponseEntity.noContent().build();
	}
	@Operation(summary = "Update an user", description = "Updates the information and resources of an user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "403", description = "The request is unauthorized", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	
	@PutMapping("/{id}")
	public UserDto updateUser(@PathVariable Long id, @RequestBody UserUpdateDto updaUserDto) throws SQLException{
		
		return userService.replaceUser(id,updaUserDto);
	}
	@Operation(summary = "Update image of user based on ID", description = "Update the image of an user whose ID matches the one on the URL.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Image updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content),
			@ApiResponse(responseCode = "405", description = "Not allowed", content = @Content) })
	
	@PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> replaceUserImage(@PathVariable long id, @RequestParam("file") MultipartFile file) {
		Optional<User> optionalUser = userService.findById(id);

		if (optionalUser.isPresent()) {
			User user = optionalUser.get();

			try {
				Blob imageBlob = new SerialBlob(file.getBytes());

				user.setImageFile(imageBlob);
				user.setImage(true); 
				userService.save(user); 

				return ResponseEntity.ok("Imagen actualizada correctamente"); // 200 OK 
			} catch (IOException | SQLException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la imagen"); // 500 Internal Server Error 
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actividad no encontrada"); // 404 Not Found 
		}
	}
}