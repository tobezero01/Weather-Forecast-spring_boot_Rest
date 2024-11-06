package com.skyapi.weatherforecast.daily;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.customFieldFilter.DailyWeatherFieldFilter;

@JsonPropertyOrder({"day_of_month","month","min_temperature","max_temperature","precipitation","status"})
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = DailyWeatherFieldFilter.class)
public class DailyWeatherDTO {

    @JsonProperty("day_of_month")
    private int dayOfMonth;
    @JsonProperty("month")
    private int month;

    @JsonProperty("min_temperature")
    private int minTemp;

    @JsonProperty("max_temperature")
    private int maxTemp;
    @JsonProperty("precipitation")
    private int precipitation;

    @JsonProperty("status")
    private String status;


    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public int getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(int precipitation) {
        this.precipitation = precipitation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DailyWeatherDTO status(String status) {
        setStatus(status);
        return this;
    }

    public DailyWeatherDTO precipitation(int precipitation) {
        setPrecipitation(precipitation);
        return this;
    }

    public DailyWeatherDTO maxTemp(int maxTemp) {
        setMaxTemp(maxTemp);
        return this;
    }

    public DailyWeatherDTO minTemp(int minTemp) {
        setMinTemp(minTemp);
        return this;
    }

    public DailyWeatherDTO dayOfMonth(int day) {
        this.setDayOfMonth(day);
        return this;
    }
    public DailyWeatherDTO month(int month) {
        this.setMonth(month);
        return this;
    }

    @Override
    public String toString() {
        return "DailyWeatherDTO{" +
                "dayOfMonth=" + dayOfMonth +
                ", month=" + month +
                ", minTemp=" + minTemp +
                ", maxTemp=" + maxTemp +
                ", precipitation=" + precipitation +
                ", status='" + status + '\'' +
                '}';
    }
}
