package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.exception.BadRequestException;
import com.skyapi.weatherforecast.exception.GeolocationException;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/hourly")
@Validated
public class HourlyWeatherApiController {
    private HourlyWeatherService hourlyWeatherService;
    private GeolocationService geolocationService;

    private ModelMapper modelMapper;

    public HourlyWeatherApiController(HourlyWeatherService hourlyWeatherService,
                                      GeolocationService geolocationService,
                                      ModelMapper modelMapper) {
        this.hourlyWeatherService = hourlyWeatherService;
        this.geolocationService = geolocationService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<?> listHourlyForecastByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIPAddress(request);

        try {
            int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
            Location locationFromIP = geolocationService.getLocation(ipAddress);

            List<HourlyWeather> hourlyForecast = hourlyWeatherService.getByLocation(locationFromIP, currentHour);

            if (hourlyForecast.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(listEntity2DTO(hourlyForecast));
        } catch (GeolocationException e) {
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> listHourlyForecastByIPAddress(@PathVariable("locationCode") String locationCode,
                                                           HttpServletRequest request) {
        try {
            int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));

            List<HourlyWeather> hourlyForecast = hourlyWeatherService.getByLocationCode(locationCode, currentHour);

            if (hourlyForecast.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(listEntity2DTO(hourlyForecast));
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NumberFormatException exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{code}")
    public ResponseEntity<?> updateHourlyForecast(@PathVariable("code") String locationCode,
                                                  @RequestBody @Valid List<HourlyWeatherDTO> listDTO) throws BadRequestException {
        if (listDTO.isEmpty()) {
            throw new BadRequestException("Hourly forecast data cannot be empty");
        }

        // Converting DTO list to entity list
        List<HourlyWeather> hourlyWeatherList = listDTO2ListEntity(listDTO);

        try {
            List<HourlyWeather> updatedHourlyWeather = hourlyWeatherService.updateByLocationCode(locationCode, hourlyWeatherList);
            return ResponseEntity.ok(listEntity2DTO(updatedHourlyWeather));
        } catch (LocationNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }

    }


    private List<HourlyWeather> listDTO2ListEntity(List<HourlyWeatherDTO> listDTO) {
        List<HourlyWeather> list = new ArrayList<>();

        listDTO.forEach(dto -> {
            list.add(modelMapper.map(dto, HourlyWeather.class));
        });
        return list;
    }

    private HourlyWeatherListDTO listEntity2DTO(List<HourlyWeather> hourlyWeathers) {
        Location location = hourlyWeathers.get(0).getId().getLocation();

        HourlyWeatherListDTO listDTO = new HourlyWeatherListDTO();
        listDTO.setLocation(location.toString());
        hourlyWeathers.forEach(hourlyWeather -> {
            HourlyWeatherDTO dto = modelMapper.map(hourlyWeather, HourlyWeatherDTO.class);
            listDTO.addHourlyWeatherDTO(dto);
        });

        return listDTO;
    }


}
