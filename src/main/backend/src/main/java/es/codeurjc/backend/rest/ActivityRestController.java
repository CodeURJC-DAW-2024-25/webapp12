package es.codeurjc.backend.rest;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import es.codeurjc.backend.dto.ActivityDto;
import es.codeurjc.backend.dto.ActivityUpdateDto;
import es.codeurjc.backend.service.ActivityService;

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
    public ResponseEntity<ActivityDto> createActivity(@RequestBody ActivityUpdateDto activityPostDto) {
        ActivityDto createdActivity = activityService.createActivity(activityPostDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdActivity);
    }
	
}