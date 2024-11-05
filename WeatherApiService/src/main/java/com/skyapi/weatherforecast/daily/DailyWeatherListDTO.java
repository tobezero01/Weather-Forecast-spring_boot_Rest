package com.skyapi.weatherforecast.daily;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyapi.weatherforecast.hourly.HourlyWeatherDTO;

import java.util.ArrayList;
import java.util.List;

public class DailyWeatherListDTO {

    private String location;

    @JsonProperty("daily_forecast")
    private List<DailyWeatherDTO> dailyForecasts = new ArrayList<>();

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<DailyWeatherDTO> getDailyForecasts() {
        return dailyForecasts;
    }

    public void setDailyForecasts(List<DailyWeatherDTO> dailyForecasts) {
        this.dailyForecasts = dailyForecasts;
    }

    public void addDailyWeatherDTO (DailyWeatherDTO dto) {
        this.dailyForecasts.add(dto);
    }

}
