package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/v1/locations")
// Kích hoạt CORS để cho phép các yêu cầu từ localhost:3000
@CrossOrigin(origins = "http://localhost:3000")
public class LocationApiController {

    private final LocationService locationService;

    public LocationApiController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<Location> addLocation(@RequestBody @Valid Location location) {
        // Kiểm tra xem địa điểm đã tồn tại chưa
        if (locationService.existsByCode(location.getCode())) {
            return ResponseEntity.badRequest().build(); // trả về 400 nếu đã tồn tại
        }

        Location addedLocation = locationService.add(location);
        URI uri = URI.create("/v1/locations/" + location.getCode());

        return ResponseEntity.created(uri).body(addedLocation);
    }


}
