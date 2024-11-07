package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.exception.GeolocationException;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Realtime Weather", description = "API for retrieving and updating real-time weather data")
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

    @Operation(
            summary = "Return current weather information based on client's IP address",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Current weather data",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RealtimeWeatherDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No weather data available"),
                    @ApiResponse(responseCode = "400", description = "Geolocation Error")
            }
    )
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

    @Operation(
            summary = "Return current weather for a specific location by code",
            parameters = {
                    @Parameter(name = "code", description = "Location code", required = true, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Current weather data for the specified location",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RealtimeWeatherDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Location not found")
            }
    )
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


    @Operation(
            summary = "Update real-time weather data for a location",
            parameters = {
                    @Parameter(name = "code", description = "Location code", required = true, schema = @Schema(type = "string"))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Realtime weather data to be updated",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RealtimeWeatherDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weather data updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RealtimeWeatherDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Location not found")
            }
    )
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
