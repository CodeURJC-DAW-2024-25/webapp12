package es.codeurjc.backend.rest;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestBody;
import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;


import es.codeurjc.backend.dto.ActivityDto;
import es.codeurjc.backend.dto.ActivityUpdateDto;
import es.codeurjc.backend.dto.NewActivityDto;
import es.codeurjc.backend.model.Activity;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.service.ActivityService;
import es.codeurjc.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/activities")
public class ActivityRestController {

	@Autowired
	private ActivityService activityService;
	@Autowired
	private UserService userService;

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
					.header(HttpHeaders.CONTENT_TYPE, "image/jpeg") 
					.contentLength(optionalActivity.get().getImageFile().length())
					.body(file);
		} else {
			return ResponseEntity.notFound().build();
		}
	}


	@DeleteMapping("/{id}/image")
	public ResponseEntity<String> deleteImage(@PathVariable long id) {
		Optional<Activity> optionalActivity = activityService.findById(id);

		if (optionalActivity.isPresent()) {
			Activity activity = optionalActivity.get();

			if (activity.getImageFile() != null) {
				activity.setImageFile(null);
				activity.setImage(false);
				activityService.save(activity);

				return ResponseEntity.ok("Imagen eliminada correctamente"); // 200 OK 
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay imagen para eliminar"); // 404 Not Found 
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actividad no encontrada"); // 404 Not Found 
		}
	}

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadImage(@PathVariable long id, @RequestParam("file") MultipartFile file) {
        Optional<Activity> optionalActivity = activityService.findById(id);

        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();

            try {
                Blob imageBlob = new SerialBlob(file.getBytes());
                activity.setImageFile(imageBlob);
                activity.setImage(true);
                activityService.save(activity);

                return ResponseEntity.ok().build(); // 200 OK
            } catch (IOException | SQLException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
            }
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found 
        }
    }

	@PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> updateImage(@PathVariable long id, @RequestParam("file") MultipartFile file) {
		Optional<Activity> optionalActivity = activityService.findById(id);

		if (optionalActivity.isPresent()) {
			Activity activity = optionalActivity.get();

			try {
				Blob imageBlob = new SerialBlob(file.getBytes());

				activity.setImageFile(imageBlob);
				activity.setImage(true); 
				activityService.save(activity); 

				return ResponseEntity.ok("Imagen actualizada correctamente"); // 200 OK 
			} catch (IOException | SQLException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la imagen"); // 500 Internal Server Error 
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actividad no encontrada"); // 404 Not Found 
		}
	}

	@PostMapping("/{id}/reserve")
	public ResponseEntity<byte[]> reserveActivity(@PathVariable Long id, Principal principal) throws IOException {
		if (principal == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
		}

		String userEmail = principal.getName();
		User user = userService.findByEmail(userEmail);

		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
		}

		boolean success = activityService.reserveActivity(id, user.getId());

		if (!success) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 Bad Request
		}

		byte[] pdfContents = activityService.generateReservationPDF(id, user.getId()).toByteArray();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Ticket_Reserva_" + user.getName() + ".pdf");

		return ResponseEntity.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_PDF)
				.body(pdfContents);
	}
	
}