package com.skyapi.weatherforecast.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @Column(length = 12, nullable = false, unique = true)
    @NotBlank
    private String code;

    @Column(length = 128, nullable = false)
    @NotBlank
    @JsonProperty("city_name")
    private String cityName;

    @Column(length = 128)
    @JsonProperty("region_name")
    @NotNull
    private String regionName;

    @Column(length = 64, nullable = false)
    @NotBlank
    @JsonProperty("country_name")
    private String countryName;

    @Column(length = 2, nullable = false)
    @NotBlank
    @JsonProperty("country_code")
    private String countryCode;

    private boolean enabled;

    @JsonIgnore // Không ánh xạ thuộc tính này sang JSON
    private boolean trashed;

    public Location() {
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

    @Override
    public String toString() {
        return "Location{" +
                "code='" + code + '\'' +
                ", cityName='" + cityName + '\'' +
                ", regionName='" + regionName + '\'' +
                ", countryName='" + countryName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", enabled=" + enabled +
                ", trashed=" + trashed +
                '}';
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
