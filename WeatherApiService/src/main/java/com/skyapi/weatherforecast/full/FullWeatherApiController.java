package com.skyapi.weatherforecast.full;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.exception.BadRequestException;
import com.skyapi.weatherforecast.exception.GeolocationException;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/full")
public class FullWeatherApiController {

    private GeolocationService geolocationService;
    private FullWeatherService fullWeatherService;
    private ModelMapper modelMapper;

    public FullWeatherApiController(GeolocationService geolocationService,
                                    FullWeatherService fullWeatherService,
                                    ModelMapper modelMapper) {
        this.geolocationService = geolocationService;
        this.fullWeatherService = fullWeatherService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<?> getFullWeatherByIPAddress(HttpServletRequest request) throws GeolocationException {
        String ipAddress = CommonUtility.getIPAddress(request);

        Location locationFromIP = geolocationService.getLocation(ipAddress);
        Location locationInDB = fullWeatherService.getByLocation(locationFromIP);

        return ResponseEntity.ok(entity2DTO(locationInDB));
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> getFullWeatherByLocationCode(@PathVariable("locationCode") String locationCode) {
        Location locationInDB = fullWeatherService.get(locationCode);
        return ResponseEntity.ok(entity2DTO(locationInDB));
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateFullWeatherByLocationCode(@PathVariable("locationCode") String locationCode ,
                                                             @RequestBody FullWeatherDTO dto) throws BadRequestException {
        if (dto.getListHourlyWeather().isEmpty()) {
            throw new BadRequestException("Hourly weather data can not be empty");
        }
        if (dto.getListDailyWeather().isEmpty()) {
            throw new BadRequestException("Daily weather data can not be empty");
        }

        Location locationInRequest = dto2Entity(dto);
        Location updatedLocation = fullWeatherService.update(locationCode, locationInRequest);
        return ResponseEntity.ok(entity2DTO(updatedLocation));
    }

    private FullWeatherDTO entity2DTO(Location entity) {
        FullWeatherDTO dto = modelMapper.map(entity, FullWeatherDTO.class);
        dto.getRealtimeWeather().setLocation(null);
        return dto;
    }

    private Location dto2Entity(FullWeatherDTO dto) {
        return modelMapper.map(dto, Location.class);
    }


}
