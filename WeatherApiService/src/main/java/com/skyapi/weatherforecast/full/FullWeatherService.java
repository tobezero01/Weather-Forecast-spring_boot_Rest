package com.skyapi.weatherforecast.full;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import org.springframework.stereotype.Service;

@Service
public class FullWeatherService {
    private LocationRepository locationRepository;

    public FullWeatherService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location getByLocation(Location locationFromIP) {
        String cityName = locationFromIP.getCityName();
        String locationCode = locationFromIP.getCountryCode();

        Location locationInDB = locationRepository.findByCountryCodeAndCityName(locationCode,cityName);

        if (locationInDB == null) {
            throw new LocationNotFoundException("No location found with the given country and city");
        }

        return locationInDB;
    }
}
