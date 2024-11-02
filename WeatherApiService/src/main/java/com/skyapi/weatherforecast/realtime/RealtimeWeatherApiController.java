package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.exception.GeolocationException;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/realtime")
public class RealtimeWeatherApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherApiController.class);

    private ModelMapper modelMapper;
    private GeolocationService geolocationService;
    private RealtimeWeatherService realtimeWeatherService;

    public RealtimeWeatherApiController(ModelMapper modelMapper, GeolocationService geolocationService,
                                        RealtimeWeatherService realtimeWeatherService) {
        this.modelMapper = modelMapper;
        this.geolocationService = geolocationService;
        this.realtimeWeatherService = realtimeWeatherService;
    }

    @GetMapping
    public ResponseEntity<?> getRealtimeWeatherByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIPAddress(request);

        try {
            Location locationFromIP = geolocationService.getLocation(ipAddress);
            RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIP);

            RealtimeWeatherDTO dto = modelMapper.map(realtimeWeather, RealtimeWeatherDTO.class);

            return ResponseEntity.ok(dto);

        } catch (GeolocationException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getRealtimeWeatherByLocationCode(@PathVariable("code") String locationCode) {
        try {
            RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocationCode(locationCode);
            return ResponseEntity.ok(entity2DTO(realtimeWeather));
        } catch (LocationNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{code}")
    public ResponseEntity<?> updateRealtimeWeather(@PathVariable("code") String locationCode,
                                                   @RequestBody @Valid RealtimeWeather realtimeWeatherInRequest) {
        realtimeWeatherInRequest.setLocationCode(locationCode);
        try {
            RealtimeWeather updatedRealtimeWeather = realtimeWeatherService.update(locationCode, realtimeWeatherInRequest);
            return ResponseEntity.ok(entity2DTO(updatedRealtimeWeather));
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private RealtimeWeatherDTO entity2DTO(RealtimeWeather realtimeWeather) {
        return modelMapper.map(realtimeWeather, RealtimeWeatherDTO.class);
    }
}
