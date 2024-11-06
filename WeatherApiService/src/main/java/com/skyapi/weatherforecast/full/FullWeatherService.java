package com.skyapi.weatherforecast.full;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
            throw new LocationNotFoundException("No location found with the given countryCode and city name");
        }

        return locationInDB;
    }

    public Location get(String locationCode) {
        Location location =  locationRepository.findByCode(locationCode);
        if (location == null) {
            throw new LocationNotFoundException("No location found with the given code");
        }

        return location;
    }

    public Location update(String locationCode, Location locationInRequest) {
        Location locationInDB = locationRepository.findByCode(locationCode);
        if (locationInDB == null) {
            throw new LocationNotFoundException("No location found with the given code");
        }

        RealtimeWeather realtimeWeather = locationInRequest.getRealtimeWeather();
        realtimeWeather.setLocation(locationInDB);

        List<DailyWeather> listDailyWeather = locationInRequest.getListDailyWeather();
        listDailyWeather.forEach(dw -> dw.getId().setLocation(locationInDB));

        List<HourlyWeather> listHourlyWeather = locationInRequest.getListHourlyWeather();
        listHourlyWeather.forEach(hw -> hw.getId().setLocation(locationInDB));

        locationInRequest.setCode(locationInDB.getCode());
        locationInRequest.setCityName(locationInDB.getCityName());
        locationInRequest.setRegionName(locationInDB.getRegionName());
        locationInRequest.setCountryName(locationInDB.getCountryName());
        locationInRequest.setCountryCode(locationInDB.getCountryCode());
        locationInRequest.setEnabled(locationInDB.isEnabled());
        locationInRequest.setTrashed(locationInDB.isTrashed());

        return locationRepository.save(locationInRequest);
    }
}
