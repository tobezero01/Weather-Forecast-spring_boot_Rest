package com.skyapi.weatherforecast.full;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyapi.weatherforecast.customFieldFilter.RealtimeWeatherFieldFilter;
import com.skyapi.weatherforecast.daily.DailyWeatherDTO;
import com.skyapi.weatherforecast.hourly.HourlyWeatherDTO;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherDTO;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

public class FullWeatherDTO {
    private String location;

    @JsonProperty("realtime_weather")
    @Valid
    private RealtimeWeatherDTO realtimeWeather = new RealtimeWeatherDTO();

    @JsonProperty("hourly_forecast")
    private List<HourlyWeatherDTO> listHourlyWeather = new ArrayList<>();
    @JsonProperty("daily_forecast")
    private List<DailyWeatherDTO> listDailyWeather = new ArrayList<>();

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public RealtimeWeatherDTO getRealtimeWeather() {
        return realtimeWeather;
    }

    public void setRealtimeWeather(RealtimeWeatherDTO realtimeWeather) {
        this.realtimeWeather = realtimeWeather;
    }

    public List<HourlyWeatherDTO> getListHourlyWeather() {
        return listHourlyWeather;
    }

    public void setListHourlyWeather(List<HourlyWeatherDTO> listHourlyWeather) {
        this.listHourlyWeather = listHourlyWeather;
    }

    public List<DailyWeatherDTO> getListDailyWeather() {
        return listDailyWeather;
    }

    public void setListDailyWeather(List<DailyWeatherDTO> listDailyWeather) {
        this.listDailyWeather = listDailyWeather;
    }
}
