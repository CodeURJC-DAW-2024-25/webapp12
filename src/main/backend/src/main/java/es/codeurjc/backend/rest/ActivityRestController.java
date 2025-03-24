package es.codeurjc.backend.rest;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import javax.sql.rowset.serial.SerialBlob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import es.codeurjc.backend.dto.ActivityDto;
import es.codeurjc.backend.dto.ActivityUpdateDto;
import es.codeurjc.backend.dto.NewActivityDto;
import es.codeurjc.backend.model.Activity;
import es.codeurjc.backend.model.Place;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.service.ActivityService;
import es.codeurjc.backend.service.PlaceService;
import es.codeurjc.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/activities")
public class ActivityRestController {

	@Autowired
	private ActivityService activityService;
	@Autowired
	private UserService userService;
	@Autowired
	private PlaceService placeService;

	@Operation(summary = "Get every activities", description = "Returns a list with every activity.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List with activities returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Activity not found", content = @Content) })
	@GetMapping("/")
	public Collection<ActivityDto> getActivities() {

		return activityService.getActivitiesDtos();
	}

	@Operation(summary = "Get activity based on ID", description = "Returns the activity whose ID matches the one on the URL.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Activity returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Activity not found", content = @Content) })
	@GetMapping("/{id}")
    public ActivityDto getActivity(@PathVariable Long id) {
        return activityService.getActivityDtoById(id);
    }

	@Operation(summary = "Create new activity", description = "Creates a new activity and returns that new activity.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Activity created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityDto.class))),
			@ApiResponse(responseCode = "403", description = "The request is unauthorized", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@PostMapping("/")
    public ResponseEntity<ActivityDto> createActivity(@RequestBody NewActivityDto activityPostDto) {
        ActivityDto createdActivity = activityService.createActivity(activityPostDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdActivity);
    }

	@Operation(summary = "Get every activities", description = "Returns a list with every activity.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List with activities returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Activity not found", content = @Content) })
	@GetMapping("/pageable")
	public ResponseEntity<Page<ActivityDto>> getActivities(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "4") int size 
	) {
		Pageable pageable = PageRequest.of(page, size);
		Page<ActivityDto> activitiesPage = activityService.getActivities(pageable);
		return ResponseEntity.ok(activitiesPage);
	}
	@Operation(summary = "Update an activity", description = "Updates the information and resources of an activity.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Activity updated successfully", content = @Content),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "403", description = "The request is unauthorized", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
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

	@Operation(summary = "Delete activity", description = "Deletes an activity and returns that deleted activity.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Activity deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content),
			@ApiResponse(responseCode = "405", description = "Not allowed", content = @Content)
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteActivity(@PathVariable Long id) {
		try {
			activityService.deleteActivity(id);
			return ResponseEntity.ok("Actividad eliminada correctamente"); 
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actividad no encontrada"); 
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la actividad"); 
		}
	}

	@Operation(summary = "Get the activity photo", description = "Returns the activity image based on the ID")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Image returned successfully", content = @Content),
		@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
		@ApiResponse(responseCode = "404", description = "Activity not found", content = @Content)
	})
	@GetMapping("/{id}/image")
	public ResponseEntity<Resource> downloadImage(@PathVariable long id) throws SQLException {
		Optional<Activity> optionalActivity = activityService.findById(id);

		if (optionalActivity.isPresent() && optionalActivity.get().getImageFile() != null) {
			Resource file = new InputStreamResource(optionalActivity.get().getImageFile().getBinaryStream());

			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_TYPE, "image/jpeg") 
					.contentLength(optionalActivity.get().getImageFile().length())
					.body(file);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@Operation(summary = "Delete activity image", description = "Deletes an activity imageand returns that deleted activity.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Activity deleted successfully"),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content),
			@ApiResponse(responseCode = "405", description = "Not allowed", content = @Content)
	})
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

	@Operation(summary = "Create new activity image", description = "Creates a new activity image and returns that new activity image.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Activity image created successfully"),
		@ApiResponse(responseCode = "403", description = "The request is unauthorized"),
		@ApiResponse(responseCode = "404", description = "Not found") 
	})				
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

	@Operation(summary = "Update image of activity based on ID", description = "Update the image of an activity whose ID matches the one on the URL.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Image updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Activity not found", content = @Content),
			@ApiResponse(responseCode = "405", description = "Not allowed", content = @Content) })
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

	@Operation(summary = "Reserve an activity", description = "A user reserve an activity and returns a .pdf with the activity information.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "User reserve an activity successfully"),
			@ApiResponse(responseCode = "403", description = "The request is unauthorized", content = @Content),
			@ApiResponse(responseCode = "404", description = "Activity not found", content = @Content) })
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

	@Operation(summary = "Search an activity by place", description = "Returns a activities list found by place.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List with activities returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Activities not found", content = @Content) })
	@GetMapping("/search")
    public ResponseEntity<?> searchActivitiesByPlace(
            @RequestParam(value = "placeId", required = false) Long placeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size
    ) {
        if (placeId == null) {
            return ResponseEntity.badRequest().body("El parámetro 'placeId' es requerido");
        }

        Optional<Place> optionalPlace = placeService.findById(placeId);

        if (optionalPlace.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lugar no encontrado");
        }

        Place place = optionalPlace.get();
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityDto> activitiesPage = activityService.findByPlace(place, pageable);

        return ResponseEntity.ok(activitiesPage);
    }

	@Operation(summary = "Get activities by user", description = "Returns a paginated list of activities in which the user is enrolled.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activities returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityDto.class))),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ActivityDto>> getActivitiesByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "2") int size) { 

        Pageable pageable = PageRequest.of(page, size);

        Page<ActivityDto> activitiesPage = activityService.getActivitiesByUser(userId, pageable);

        return ResponseEntity.ok(activitiesPage);
    }

	@Operation(summary = "Get activities recommended by user", description = "Returns a paginated list of activities recommended in which the user is enrolled.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activities recommended returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityDto.class))),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
	@GetMapping("/users/{userId}/recommended-activities")
    public ResponseEntity<Page<ActivityDto>> getRecommendedActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ActivityDto> recommendedActivities = activityService.recommendActivities(userId, pageable);

        return ResponseEntity.ok(recommendedActivities);
    }
}