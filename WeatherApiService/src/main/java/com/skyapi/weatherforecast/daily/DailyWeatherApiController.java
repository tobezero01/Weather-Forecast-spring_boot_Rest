package com.skyapi.weatherforecast.daily;

import com.skyapi.weatherforecast.full.FullWeatherApiController;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.exception.BadRequestException;
import com.skyapi.weatherforecast.exception.GeolocationException;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/daily")
@Validated
public class DailyWeatherApiController {
    private DailyWeatherService dailyWeatherService;
    private GeolocationService geolocationService;

    private ModelMapper modelMapper;

    public DailyWeatherApiController(DailyWeatherService dailyWeatherService,
                                     GeolocationService geolocationService,
                                     ModelMapper modelMapper) {
        this.dailyWeatherService = dailyWeatherService;
        this.geolocationService = geolocationService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Return daily weather information based on client's IP address", tags = { "Daily Forecast" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daily weather data for the location based on IP"),
            @ApiResponse(responseCode = "204", description = "No forecast data available"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @GetMapping
    public ResponseEntity<?> listDailyForecastByIPAddress(HttpServletRequest request) throws GeolocationException {
        String ipAddress = CommonUtility.getIPAddress(request);

        Location locationFromIP = geolocationService.getLocation(ipAddress);

        List<DailyWeather> dailyWeathers = dailyWeatherService.getByLocation(locationFromIP);

        if (dailyWeathers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        DailyWeatherListDTO dto = listEntity2DTO(dailyWeathers);
        return ResponseEntity.ok(addLinksByIP(dto));
    }

    @Operation(summary = "Return daily weather forecast information for a specific location code", tags = { "Daily Forecast" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daily forecast data for the specified location"),
            @ApiResponse(responseCode = "204", description = "No forecast data available"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @GetMapping("/{locationCode}")
    public ResponseEntity<?> listDailyForecastByLocationCode(@PathVariable("locationCode") String locationCode)  {
        List<DailyWeather> dailyWeathers = dailyWeatherService.getByLocationCode(locationCode);

        if (dailyWeathers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        DailyWeatherListDTO dto = listEntity2DTO(dailyWeathers);
        return ResponseEntity.ok(addLinksByLocationCode(dto, locationCode));
    }
    @Operation(summary = "Update daily weather forecast information for a specific location by code", tags = { "Daily Forecast" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daily forecast data updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid data provided"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateDailyForecast(@PathVariable("locationCode") String locationCode,
                                                 @RequestBody @Valid List<DailyWeatherDTO> listDTO) throws BadRequestException {
        if (listDTO.isEmpty()) {
            throw new BadRequestException("Daily forecast data cannot be empty");
        }

        // Converting DTO list to entity list
        List<DailyWeather> dailyWeatherList = listDTO2ListEntity(listDTO);

        List<DailyWeather> updatedForecast = dailyWeatherService.updateByLocationCode(locationCode, dailyWeatherList);
        DailyWeatherListDTO dto = listEntity2DTO(updatedForecast);
        return ResponseEntity.ok(addLinksByLocationCode(dto, locationCode));

    }

    private DailyWeatherListDTO listEntity2DTO(List<DailyWeather> dailyForecast) {
        Location location = dailyForecast.get(0).getId().getLocation();

        DailyWeatherListDTO listDTO = new DailyWeatherListDTO();
        listDTO.setLocation(location.toString());
        dailyForecast.forEach(dailyWeather -> {
            DailyWeatherDTO dto = modelMapper.map(dailyWeather, DailyWeatherDTO.class);
            listDTO.addDailyWeatherDTO(dto);
        });

        return listDTO;
    }

    private List<DailyWeather> listDTO2ListEntity(List<DailyWeatherDTO> listDTO) {
        List<DailyWeather> list = new ArrayList<>();

        listDTO.forEach(dto -> {
            list.add(modelMapper.map(dto, DailyWeather.class));
        });
        return list;
    }

    private EntityModel<DailyWeatherListDTO> addLinksByIP(DailyWeatherListDTO dto) throws GeolocationException {
        EntityModel<DailyWeatherListDTO> entityModel = EntityModel.of(dto);
        entityModel.add(linkTo(methodOn(DailyWeatherApiController.class).listDailyForecastByIPAddress(null)).withSelfRel());
        entityModel.add(linkTo(methodOn(HourlyWeatherApiController.class).listHourlyForecastByIPAddress(null)).withRel("hourly_forecast"));
        entityModel.add(linkTo(methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByIPAddress(null)).withRel("realtime_weather"));
        entityModel.add(linkTo(methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null)).withRel("full_weather"));
        return entityModel;
    }

    private EntityModel<DailyWeatherListDTO> addLinksByLocationCode(DailyWeatherListDTO dto, String locationCode)   {
        EntityModel<DailyWeatherListDTO> entityModel = EntityModel.of(dto);
        entityModel.add(linkTo(methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(locationCode)).withSelfRel());
        entityModel.add(linkTo(methodOn(HourlyWeatherApiController.class).listHourlyForecastByLocationCode(locationCode, null)).withRel("hourly_forecast"));
        entityModel.add(linkTo(methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByLocationCode(locationCode)).withRel("realtime_weather"));
        entityModel.add(linkTo(methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(locationCode)).withRel("full_weather"));
        return entityModel;
    }

}
