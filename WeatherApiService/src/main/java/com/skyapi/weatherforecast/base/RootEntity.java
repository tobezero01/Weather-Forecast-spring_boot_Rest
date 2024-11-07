package com.skyapi.weatherforecast.base;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RootEntity {
    @JsonProperty("location_url")
    private String locationUrl;
    @JsonProperty("location_by_code_url")
    private String locationByCodeUrl;
    @JsonProperty("realtime_weather_by_ip_url")
    private String realtimeWeatherByIpUrl;

    @JsonProperty("realtime_weather_by_code_url")
    private String realtimeWeatherByCodeUrl;

    @JsonProperty("hourly_forecast_by_ip_url")
    private String hourlyForecastByIpUrl;

    @JsonProperty("hourly_forecast_by_code_url")
    private String hourlyForecastByCodeUrl;

    @JsonProperty("daily_forecast_by_ip_url")
    private String dailyForecastByIpUrl;

    @JsonProperty("daily_forecast_by_code_url")
    private String dailyForecastByCodeUrl;

    @JsonProperty("full_weather_by_ip_url")
    private String fullWeatherByIpUrl;

    @JsonProperty("full_weather_by_code_url")
    private String fullWeatherByCodeUrl;

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public String getLocationByCodeUrl() {
        return locationByCodeUrl;
    }

    public void setLocationByCodeUrl(String locationByCodeUrl) {
        this.locationByCodeUrl = locationByCodeUrl;
    }

    public String getRealtimeWeatherByIpUrl() {
        return realtimeWeatherByIpUrl;
    }

    public void setRealtimeWeatherByIpUrl(String realtimeWeatherByIpUrl) {
        this.realtimeWeatherByIpUrl = realtimeWeatherByIpUrl;
    }

    public String getRealtimeWeatherByCodeUrl() {
        return realtimeWeatherByCodeUrl;
    }

    public void setRealtimeWeatherByCodeUrl(String realtimeWeatherByCodeUrl) {
        this.realtimeWeatherByCodeUrl = realtimeWeatherByCodeUrl;
    }

    public String getHourlyForecastByIpUrl() {
        return hourlyForecastByIpUrl;
    }

    public void setHourlyForecastByIpUrl(String hourlyForecastByIpUrl) {
        this.hourlyForecastByIpUrl = hourlyForecastByIpUrl;
    }

    public String getHourlyForecastByCodeUrl() {
        return hourlyForecastByCodeUrl;
    }

    public void setHourlyForecastByCodeUrl(String hourlyForecastByCodeUrl) {
        this.hourlyForecastByCodeUrl = hourlyForecastByCodeUrl;
    }

    public String getDailyForecastByIpUrl() {
        return dailyForecastByIpUrl;
    }

    public void setDailyForecastByIpUrl(String dailyForecastByIpUrl) {
        this.dailyForecastByIpUrl = dailyForecastByIpUrl;
    }

    public String getDailyForecastByCodeUrl() {
        return dailyForecastByCodeUrl;
    }

    public void setDailyForecastByCodeUrl(String dailyForecastByCodeUrl) {
        this.dailyForecastByCodeUrl = dailyForecastByCodeUrl;
    }

    public String getFullWeatherByIpUrl() {
        return fullWeatherByIpUrl;
    }

    public void setFullWeatherByIpUrl(String fullWeatherByIpUrl) {
        this.fullWeatherByIpUrl = fullWeatherByIpUrl;
    }

    public String getFullWeatherByCodeUrl() {
        return fullWeatherByCodeUrl;
    }

    public void setFullWeatherByCodeUrl(String fullWeatherByCodeUrl) {
        this.fullWeatherByCodeUrl = fullWeatherByCodeUrl;
    }
}
