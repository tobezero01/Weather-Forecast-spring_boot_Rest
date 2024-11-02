package com.skyapi.weatherforecast.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

@Entity
@Table(name = "realtime_weather")
public class RealtimeWeather {

    @Id
    @Column(name = "location_code")
    @JsonIgnore
    private String locationCode;

    @NotNull
    @Range(min = -50, max = 50, message = "Temperature must be in the range of -50 to 50 Celsius degree")
    private int temperature;

    @NotNull
    @Range(min = 0, max = 100, message = "Humidity must be in the range of 0 to 100 Percentage")
    private int humidity;

    @NotNull
    @Range(min = 0, max = 100, message = "Precipitation must be in the range of 0 to 100 Percentage")
    private int precipitation;

    @NotNull
    @JsonProperty("wind_speed")
    @Range(min = 0, max = 200, message = "Wind Speed must be in the range of 0 to 200 km/h")
    private int windSpeed;

    @Column(length = 50)
    @JsonProperty("status")
    @NotBlank(message = "Status not be blank")
    @Length(min = 3, max = 50, message = "Status must be between 3-50 characters")
    private String status;

    @JsonIgnore
    private Date lastUpdated;

    @OneToOne(cascade = CascadeType.ALL) // Adjust according to your application logic
    @MapsId
    @JsonIgnore
    @JoinColumn(name = "location_code")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealtimeWeather that)) return false;

        return getLocationCode().equals(that.getLocationCode());
    }

    @Override
    public int hashCode() {
        return getLocationCode().hashCode();
    }
}
