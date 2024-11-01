package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RealtimeWeatherService {
    private RealtimeWeatherRepository realtimeWeatherRepository;

    public RealtimeWeatherService(RealtimeWeatherRepository realtimeWeatherRepository) {
        this.realtimeWeatherRepository = realtimeWeatherRepository;
    }

    public RealtimeWeather getByLocation(Location location) throws LocationNotFoundException {
        String countryCode = location.getCountryCode();
        String city = location.getCityName();

        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByCountryCodeAndCity(countryCode, city);
        if (realtimeWeather == null) {
            throw new LocationNotFoundException("No location found with the given country and city");
        }

        return realtimeWeather;
    }


}
