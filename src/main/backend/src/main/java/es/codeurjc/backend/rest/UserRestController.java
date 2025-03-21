package es.codeurjc.backend.rest;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpHeaders;
import java.sql.SQLException;
import java.util.Collection;

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

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.backend.dto.ActivityDto;
import es.codeurjc.backend.dto.NewUserDto;
import es.codeurjc.backend.dto.UserDto;
import es.codeurjc.backend.dto.UserUpdateDto;
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

	 @GetMapping("/pageable")
    public Page<UserDto> getUsers(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);  // Paginación de 10 elementos por página
        return userService.getAllUsersPaginated(pageable);
    }

	@GetMapping("/{id}")
	public UserDto getUser(@PathVariable Long id) {
		return userService.getUserDto(id);
	}
	
	@GetMapping("/{id}/image")
	public ResponseEntity<Object> getUserImage(@PathVariable Long id) throws SQLException,IOException {
		Resource postImage = userService.getUserImageDto(id);
		return ResponseEntity
			.ok()
			.header(HttpHeaders.CONTENT_TYPE,"image/jpeg")
			.body(postImage);
	}

	@PostMapping("/")
	public ResponseEntity<UserDto> createUser(@RequestBody NewUserDto newUserDto){
        UserDto userDto = userService.createUser(newUserDto);

		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(userDto.id()).toUri();

		return ResponseEntity.created(location).body(userDto);
    } 
	@Operation(summary = "Delete user", description = "Users an activity and returns that deleted user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityDto.class))),
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

	@PutMapping("/{id}")
	public UserDto updateUser(@PathVariable Long id, @RequestBody UserUpdateDto updaUserDto) throws SQLException{
		
		return userService.replaceUser(id,updaUserDto);
	}

	@PutMapping("/{id}/image")
	public ResponseEntity<Object> replaceUserImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
			throws IOException {

		userService.replaceUserImage(id, imageFile.getInputStream(), imageFile.getSize());

		return ResponseEntity.noContent().build();
	}
}