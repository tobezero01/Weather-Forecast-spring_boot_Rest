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
}
