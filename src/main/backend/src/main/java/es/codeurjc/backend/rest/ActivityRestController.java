package es.codeurjc.backend.rest;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import es.codeurjc.backend.dto.ActivityDto;
import es.codeurjc.backend.dto.ActivityUpdateDto;
import es.codeurjc.backend.dto.NewActivityDto;
import es.codeurjc.backend.model.Activity;
import es.codeurjc.backend.service.ActivityService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/activities")
public class ActivityRestController {

	@Autowired
	private ActivityService activityService;

	@GetMapping("/")
	public Collection<ActivityDto> getActivities() {

		return activityService.getActivitiesDtos();
	}

	@GetMapping("/{id}")
    public ActivityDto getActivity(@PathVariable Long id) {
        return activityService.getActivityDtoById(id);
    }

	@PostMapping("/")
    public ResponseEntity<ActivityDto> createActivity(@RequestBody NewActivityDto activityPostDto) {
        ActivityDto createdActivity = activityService.createActivity(activityPostDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdActivity);
    }

	@PutMapping("/{id}")
	public ResponseEntity<ActivityDto> updateActivity(@PathVariable Long id, @RequestBody ActivityUpdateDto activityUpdateDto) {
		try {
			ActivityDto updatedActivityDto = activityService.updateActivity(id, activityUpdateDto);
			return ResponseEntity.ok(updatedActivityDto);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteActivity(@PathVariable Long id) {
		try {
			activityService.deleteActivity(id);
			return ResponseEntity.ok("Actividad eliminada correctamente"); // 200 OK con mensaje
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actividad no encontrada"); // 404 Not Found con mensaje
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la actividad"); // 500 Internal Server Error con mensaje
		}
	}

	@GetMapping("/{id}/image")
	public ResponseEntity<Resource> downloadImage(@PathVariable long id) throws SQLException {
		// Buscar la actividad por ID
		Optional<Activity> optionalActivity = activityService.findById(id);

		if (optionalActivity.isPresent() && optionalActivity.get().getImageFile() != null) {
			// Obtener la imagen como un recurso
			Resource file = new InputStreamResource(optionalActivity.get().getImageFile().getBinaryStream());

			// Devolver la imagen en la respuesta
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_TYPE, "image/jpeg") // Ajusta el tipo MIME según el formato de la imagen
					.contentLength(optionalActivity.get().getImageFile().length())
					.body(file);
		} else {
			// Si no se encuentra la actividad o no tiene imagen, devolver 404 Not Found
			return ResponseEntity.notFound().build();
		}
	}
	
}