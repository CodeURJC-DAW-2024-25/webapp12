package es.codeurjc.backend.rest;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	
}