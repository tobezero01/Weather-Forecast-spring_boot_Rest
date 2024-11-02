package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HourlyWeatherService {
    private HourlyWeatherRepository hourlyWeatherRepository;
    private LocationRepository locationRepository;

    public HourlyWeatherService(HourlyWeatherRepository hourlyWeatherRepository,
                                LocationRepository locationRepository) {
        this.hourlyWeatherRepository = hourlyWeatherRepository;
        this.locationRepository = locationRepository;
    }

    public List<HourlyWeather> getByLocation(Location location, int currentHour) throws LocationNotFoundException {
        Location locationInDB = locationRepository.findByCountryCodeAndCityName(
                location.getCountryCode(), location.getCityName()
        );
        if (locationInDB == null) {
            throw new LocationNotFoundException("Location not found with country code and city name");
        }

        return hourlyWeatherRepository.findByLocationCode(locationInDB.getCode(), currentHour);
    }
}
