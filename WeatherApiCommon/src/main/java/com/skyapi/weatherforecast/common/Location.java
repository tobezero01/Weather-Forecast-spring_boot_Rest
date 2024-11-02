package com.skyapi.weatherforecast.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @Column(length = 12, nullable = false, unique = true)
    @NotBlank(message = "Code is required and cannot be blank")
    @Size(max = 12, message = "Code cannot be longer than 12 characters")
    private String code;

    @Column(length = 128, nullable = false)
    @NotBlank(message = "City name is required and cannot be blank")
    @Size(max = 128, message = "City name cannot be longer than 128 characters")
    @JsonProperty("city_name")
    private String cityName;

    @Column(length = 128)
    @NotNull(message = "Region name cannot be null")
    @Size(max = 128, message = "Region name cannot be longer than 128 characters")
    @JsonProperty("region_name")
    private String regionName;

    @Column(length = 64, nullable = false)
    @NotBlank(message = "Country name is required and cannot be blank")
    @Size(max = 64, message = "Country name cannot be longer than 64 characters")
    @JsonProperty("country_name")
    private String countryName;

    @Column(length = 2, nullable = false)
    @NotBlank(message = "Country code is required and cannot be blank")
    @Size(min = 2, max = 3, message = "Country code must be exactly 2 or 3 characters")
    @JsonProperty("country_code")
    private String countryCode;

    private boolean enabled;

    @JsonIgnore
    private boolean trashed;

    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL)
    @JsonBackReference // This prevents infinite recursion during serialization
    private RealtimeWeather realtimeWeather;

    @OneToMany(mappedBy = "id.location", cascade = CascadeType.ALL)
    private List<HourlyWeather> listHourlyWeather;


    public Location() {
    }

    public Location(String cityName, String regionName, String countryName, String countryCode) {
        this.cityName = cityName;
        this.regionName = regionName;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    public Location(String code, String cityName) {
        this.code = code;
        this.cityName = cityName;
    }

    public Location(String code, String cityName, String regionName, String countryName, String countryCode, boolean enabled, boolean trashed) {
        this.code = code;
        this.cityName = cityName;
        this.regionName = regionName;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.enabled = enabled;
        this.trashed = trashed;
    }

    public Location(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isTrashed() {
        return trashed;
    }

    public void setTrashed(boolean trashed) {
        this.trashed = trashed;
    }

    public RealtimeWeather getRealtimeWeather() {
        return realtimeWeather;
    }

    public void setRealtimeWeather(RealtimeWeather realtimeWeather) {
        this.realtimeWeather = realtimeWeather;
    }

    public List<HourlyWeather> getListHourlyWeather() {
        return listHourlyWeather;
    }

    public Location code(String code) {
        setCode(code);
        return this;
    }

    public void setListHourlyWeather(List<HourlyWeather> listHourlyWeather) {
        this.listHourlyWeather = listHourlyWeather;
    }

    @Override
    public String toString() {
        return cityName + ", " + (regionName != null ? regionName : "") + ", " + countryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location location)) return false;

        return getCode().equals(location.getCode());
    }

    @Override
    public int hashCode() {
        return getCode().hashCode();
    }


}
