package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/locations")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Location", description = "API for managing locations")
public class LocationApiController {

    private  LocationService locationService;

    public LocationApiController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(summary = "Add a new location", description = "Create a new location if the code does not exist.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Location created successfully",
                    content = @Content(schema = @Schema(implementation = Location.class))),
            @ApiResponse(responseCode = "400", description = "Location with the given code already exists")
    })
    @PostMapping
    public ResponseEntity<Location> addLocation(@RequestBody @Valid Location location) {
        if (locationService.existsByCode(location.getCode())) {
            return ResponseEntity.badRequest().build();
        }
        Location addedLocation = locationService.add(location);
        URI uri = URI.create("/v1/locations/" + location.getCode());

        return ResponseEntity.created(uri).body(addedLocation);
    }


    @Operation(summary = "Get all locations", description = "Retrieve the list of all available locations.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of locations retrieved",
                    content = @Content(schema = @Schema(implementation = Location.class, type = "array"))),
            @ApiResponse(responseCode = "204", description = "No content available")
    })
    @GetMapping
    public ResponseEntity<?> listLocations() {
        List<Location> locations = locationService.list();
        if (locations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(locations);
    }
}
