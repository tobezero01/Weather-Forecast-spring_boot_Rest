package com.skyapi.weatherforecast.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Embeddable
public class DailyWeatherId implements Serializable {

    @JsonProperty("day_of_month")
    private int dayOfMonth;
    @JsonProperty("month")
    private int month;

    @ManyToOne
    @JoinColumn(name = "location_code")
    private Location location;

    public DailyWeatherId() {
    }

    public DailyWeatherId(int dayOfMonth, int month, Location location) {
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.location = location;
    }

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
