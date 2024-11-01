package com.skyapi.weatherforecast.realtime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RealtimeWeatherService {
    private RealtimeWeatherRepository realtimeWeatherRepository;

    public RealtimeWeatherService(RealtimeWeatherRepository realtimeWeatherRepository) {
        this.realtimeWeatherRepository = realtimeWeatherRepository;
    }


}
