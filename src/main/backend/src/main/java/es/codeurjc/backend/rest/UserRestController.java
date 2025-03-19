package es.codeurjc.backend.rest;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpHeaders;
import java.sql.SQLException;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.backend.dto.NewUserDto;
import es.codeurjc.backend.dto.UserDto;
import es.codeurjc.backend.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;





@RestController
@RequestMapping("/api/users")
public class UserRestController {

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public Collection<UserDto> getUsers() {

		return userService.getUsersDtos();
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
	
}