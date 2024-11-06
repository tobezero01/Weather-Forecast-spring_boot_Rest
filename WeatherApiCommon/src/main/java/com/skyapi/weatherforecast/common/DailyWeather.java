package com.skyapi.weatherforecast.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "daily_weather")
public class DailyWeather {

    @EmbeddedId
    private DailyWeatherId id = new DailyWeatherId();

    @JsonProperty("min_temperature")
    private int minTemp;

    @JsonProperty("max_temperature")
    private int maxTemp;

    @JsonProperty("precipitation")
    @Range(min = 0, max = 100, message = "Precipitation must be in the range of 0 to 100 Percentage")
    private int precipitation;

    @Column(length = 50)
    @JsonProperty("status")
    @Length(min = 3, max = 50, message = "Status must be between 3-50 characters")
    private String status;

    public DailyWeather() {
    }

    public DailyWeatherId getId() {
        return id;
    }

    public void setId(DailyWeatherId id) {
        this.id = id;
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

    public DailyWeather location(Location location) {
        this.id.setLocation(location);
        return this;
    }

    public DailyWeather status(String status) {
        setStatus(status);
        return this;
    }

    public DailyWeather precipitation(int precipitation) {
        setPrecipitation(precipitation);
        return this;
    }

    public DailyWeather maxTemp(int maxTemp) {
        setMaxTemp(maxTemp);
        return this;
    }

    public DailyWeather minTemp(int minTemp) {
        setMinTemp(minTemp);
        return this;
    }

    public DailyWeather dayOfMonth(int day) {
        this.id.setDayOfMonth(day);
        return this;
    }
    public DailyWeather month(int month) {
        this.id.setMonth(month);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyWeather that)) return false;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
    public DailyWeather getShallowCopy() {
        DailyWeather copy = new DailyWeather();
        copy.setId(this.getId());
        return copy;
    }

}
