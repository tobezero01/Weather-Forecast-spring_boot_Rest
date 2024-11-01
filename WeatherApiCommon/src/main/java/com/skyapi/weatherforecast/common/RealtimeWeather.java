package com.skyapi.weatherforecast.common;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "realtime_weather")
public class RealtimeWeather {

    @Id
    @Column(name = "location_code")
    @JsonProperty("location_code")
    private String locationCode;

    @NotNull
    @JsonProperty("temperature")
    private int temperature;

    @NotNull
    @JsonProperty("humidity")
    private int humidity;

    @NotNull
    @JsonProperty("precipitation")
    private int precipitation;

    @NotNull
    @JsonProperty("wind_speed")
    private int windSpeed;

    @Column(length = 50)
    @JsonProperty("status")
    private String status;

    @JsonProperty("last_update")
    private Date lastUpdated;

    @OneToOne(cascade = CascadeType.ALL) // Adjust according to your application logic
    @JoinColumn(name = "location_code")
    @MapsId
    @JsonManagedReference // This prevents infinite recursion during serialization
    private Location location;

    public RealtimeWeather() {
        this.lastUpdated = new Date(); // Initialize lastUpdated with the current date
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(int precipitation) {
        this.precipitation = precipitation;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.locationCode = location.getCode();
        this.location = location;
    }

    @Override
    public String toString() {
        return "RealtimeWeather{" +
                "locationCode='" + locationCode + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", precipitation=" + precipitation +
                ", windSpeed=" + windSpeed +
                ", status='" + status + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", location=" + location.toString() +
                '}';
    }
}
