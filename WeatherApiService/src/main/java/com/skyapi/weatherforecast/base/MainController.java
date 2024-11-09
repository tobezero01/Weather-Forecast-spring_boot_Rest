package com.skyapi.weatherforecast.base;

import com.skyapi.weatherforecast.daily.DailyWeatherApiController;
import com.skyapi.weatherforecast.exception.BadRequestException;
import com.skyapi.weatherforecast.exception.GeolocationException;
import com.skyapi.weatherforecast.full.FullWeatherApiController;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import com.skyapi.weatherforecast.location.LocationApiController;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherApiController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class MainController {

    @GetMapping("/")
    public ResponseEntity<RootEntity> handleBaseURI() throws GeolocationException, BadRequestException {
        return ResponseEntity.ok(createRootEntity());
    }

    private RootEntity createRootEntity() throws GeolocationException, BadRequestException {
        RootEntity entity = new RootEntity();

        String locationUrl = linkTo(methodOn(LocationApiController.class).listLocations(null, null, null, null, null, null)).toString();
        String locationByCodeUrl = linkTo(methodOn(LocationApiController.class).getLocationByCode(null)).toString();
        String realtimeWeatherByIpUrl = linkTo(methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByIPAddress(null)).toString();
        String realtimeWeatherByCodeUrl = linkTo(methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByLocationCode(null)).toString();
        String hourlyForecastByIpUrl = linkTo(methodOn(HourlyWeatherApiController.class).listHourlyForecastByIPAddress(null)).toString();
        String hourlyForecastByCodeUrl = linkTo(methodOn(HourlyWeatherApiController.class).listHourlyForecastByLocationCode(null, null)).toString();
        String dailyForecastByIpUrl = linkTo(methodOn(DailyWeatherApiController.class).listDailyForecastByIPAddress(null)).toString();
        String dailyForecastByCodeUrl = linkTo(methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(null)).toString();
        String fullWeatherByIpUrl = linkTo(methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null)).toString();
        String fullWeatherByCodeUrl = linkTo(methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(null)).toString();

        entity.setLocationByCodeUrl(locationByCodeUrl);
        entity.setLocationUrl(locationUrl);
        entity.setRealtimeWeatherByIpUrl(realtimeWeatherByIpUrl);
        entity.setRealtimeWeatherByCodeUrl(realtimeWeatherByCodeUrl);
        entity.setHourlyForecastByIpUrl(hourlyForecastByIpUrl);
        entity.setHourlyForecastByCodeUrl(hourlyForecastByCodeUrl);
        entity.setDailyForecastByIpUrl(dailyForecastByIpUrl);
        entity.setDailyForecastByCodeUrl(dailyForecastByCodeUrl);
        entity.setFullWeatherByIpUrl(fullWeatherByIpUrl);
        entity.setFullWeatherByCodeUrl(fullWeatherByCodeUrl);
        return entity;
    }
}
