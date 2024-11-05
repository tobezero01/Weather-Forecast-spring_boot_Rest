package com.skyapi.weatherforecast.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Embeddable
public class HourlyWeatherId implements Serializable {
    @JsonProperty("hour_of_day")
    private int hourOfDay;
    @ManyToOne
    @JoinColumn(name = "location_code")
    private Location location;

    public HourlyWeatherId() {
    }

    public HourlyWeatherId(int hourOfDay, Location location) {
        this.hourOfDay = hourOfDay;
        this.location = location;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HourlyWeatherId that)) return false;

        if (getHourOfDay() != that.getHourOfDay()) return false;
        return getLocation() != null ? getLocation().equals(that.getLocation()) : that.getLocation() == null;
    }

    @Override
    public int hashCode() {
        int result = getHourOfDay();
        result = 31 * result + (getLocation() != null ? getLocation().hashCode() : 0);
        return result;
    }
}
