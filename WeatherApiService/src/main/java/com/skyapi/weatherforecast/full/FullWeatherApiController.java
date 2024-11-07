package com.skyapi.weatherforecast.full;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.daily.DailyWeatherApiController;
import com.skyapi.weatherforecast.daily.DailyWeatherListDTO;
import com.skyapi.weatherforecast.exception.BadRequestException;
import com.skyapi.weatherforecast.exception.GeolocationException;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherApiController;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
@RestController
@RequestMapping("/v1/full")
public class FullWeatherApiController {

    private GeolocationService geolocationService;
    private FullWeatherService fullWeatherService;
    private ModelMapper modelMapper;

    private FullWeatherModelAssembler modelAssembler;

    public FullWeatherApiController(GeolocationService geolocationService, FullWeatherService fullWeatherService,
                                    ModelMapper modelMapper, FullWeatherModelAssembler modelAssembler) {
        this.geolocationService = geolocationService;
        this.fullWeatherService = fullWeatherService;
        this.modelMapper = modelMapper;
        this.modelAssembler = modelAssembler;
    }

    @Operation(summary = "Return full weather forecast information based on client's IP address", tags = { "Full Weather Forecast" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Full weather forecast data"),
            @ApiResponse(responseCode = "204", description = "No forecast data available"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @GetMapping
    public ResponseEntity<?> getFullWeatherByIPAddress(HttpServletRequest request) throws GeolocationException {
        String ipAddress = CommonUtility.getIPAddress(request);

        Location locationFromIP = geolocationService.getLocation(ipAddress);
        Location locationInDB = fullWeatherService.getByLocation(locationFromIP);

        FullWeatherDTO dto = entity2DTO(locationInDB);
        return ResponseEntity.ok(modelAssembler.toModel(dto));
    }

    @Operation(summary = "Return full weather forecast information for a specific location code", tags = { "Full Weather Forecast" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Full weather forecast data for the specified location"),
            @ApiResponse(responseCode = "204", description = "No forecast data available"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @GetMapping("/{locationCode}")
    public ResponseEntity<?> getFullWeatherByLocationCode(@PathVariable("locationCode") String locationCode) {
        Location locationInDB = fullWeatherService.get(locationCode);
        FullWeatherDTO dto = entity2DTO(locationInDB);
        return ResponseEntity.ok(addLinksByLocationCode(dto, locationCode));
    }

    @Operation(summary = "Update full weather forecast information for a specific location by code", tags = { "Full Weather Forecast" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Full weather forecast data updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid data provided"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
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
        FullWeatherDTO updateDto = entity2DTO(updatedLocation);
        return ResponseEntity.ok(addLinksByLocationCode(updateDto, locationCode));
    }

    private FullWeatherDTO entity2DTO(Location entity) {
        FullWeatherDTO dto = modelMapper.map(entity, FullWeatherDTO.class);
        dto.getRealtimeWeather().setLocation(null);
        return dto;
    }

    private Location dto2Entity(FullWeatherDTO dto) {
        return modelMapper.map(dto, Location.class);
    }

    private EntityModel<FullWeatherDTO> addLinksByLocationCode(FullWeatherDTO dto, String locationCode)   {
        EntityModel<FullWeatherDTO> entityModel = EntityModel.of(dto);
        entityModel.add(linkTo(methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(locationCode)).withSelfRel());
        return entityModel;
    }
}
