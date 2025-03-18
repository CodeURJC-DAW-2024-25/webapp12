package es.codeurjc.backend.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.backend.dto.ActivityResponse;
import es.codeurjc.backend.model.Activity;
import es.codeurjc.backend.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api")
public class ActivityRestController {
    
    @Autowired
    private ActivityService activityService;
    
    @Operation(summary = "Get activity based on ID", description = "Returns the activity whose ID matches the one on the URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Activity.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Activity not found", content = @Content) })
    @GetMapping(value = "/activities/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActivity(@PathVariable Long id) {
        if (activityService.exists(id)) {
            Activity activity = activityService.findById(id).orElseThrow();
            ActivityResponse activityResponse = new ActivityResponse(activity);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(activityResponse);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"Activity not found\"}");
        }
    }
}