package com.skyapi.weatherforecast.common;

import jakarta.persistence.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "hourly_weather")
public class HourlyWeather {

    @EmbeddedId
    private HourlyWeatherId id = new HourlyWeatherId();

    @Range(min = -50, max = 50, message = "Temperature must be in the range of -50 to 50 Celsius degree")
    private int temperature;

    @Range(min = 0, max = 100, message = "Precipitation must be in the range of 0 to 100 Percentage")
    private int precipitation;

    @Column(length = 50)
    @Length(min = 3, max = 50, message = "Status must be between 3-50 characters")
    private String status;


    public HourlyWeatherId getId() {
        return id;
    }

    public void setId(HourlyWeatherId id) {
        this.id = id;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
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

    public HourlyWeather id(Location location, int hour) {
        this.id.setHourOfDay(hour);
        this.id.setLocation(location);
        return this;
    }

    public HourlyWeather location(Location location) {
        this.id.setLocation(location);
        return this;
    }

    public HourlyWeather temperature(int temperature) {
        setTemperature(temperature);
        return this;
    }

    public HourlyWeather status(String status) {
        setStatus(status);
        return this;
    }

    public HourlyWeather precipitation(int precipitation) {
        setPrecipitation(precipitation);
        return this;
    }

    public HourlyWeather hourOfDay(int hourOfDay) {
        this.id.setHourOfDay(hourOfDay);
        return this;
    }

    @Override
    public String toString() {
        return "HourlyWeather{" +
                "hourOfDay=" + id.getHourOfDay() +
                ", temperature=" + temperature +
                ", precipitation=" + precipitation +
                ", status='" + status + '\'' +
                '}';
    }
}