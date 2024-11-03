package com.skyapi.weatherforecast.hourly;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.exception.GeolocationException;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HourlyWeatherApiController.class)
public class HourlyWeatherApiControllerTests {

    private static final String END_POINT_PATH = "/v1/hourly";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private HourlyWeatherService hourlyWeatherService;

    @MockBean
    private GeolocationService geolocationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listHourlyForecastByIPAddress_shouldReturnHourlyForecast() throws Exception {
        // Arrange
        String ipAddress = "203.210.142.42";
        int currentHour = 10;

        Location locationFromIP = new Location();
        locationFromIP.setCode("LOC001");
        locationFromIP.setCityName("Hanoi");

        List<HourlyWeather> hourlyForecast = new ArrayList<>();
        hourlyForecast.add(new HourlyWeather().hourOfDay(11).temperature(25).status("Sunny").precipitation(10));

        when(geolocationService.getLocation(anyString())).thenReturn(locationFromIP);
        when(hourlyWeatherService.getByLocation(locationFromIP, currentHour)).thenReturn(hourlyForecast);

        // Act & Assert
        mockMvc.perform(get(END_POINT_PATH)
                        .header("X-Current-Hour", String.valueOf(currentHour))
                        .header("X-Forwarded-For".toUpperCase(), ipAddress)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].temperature").value(25))
                .andExpect(jsonPath("$[0].status").value("Sunny"))
                .andExpect(jsonPath("$[0].precipitation").value(10));
    }

    @Test
    void listHourlyForecastByIPAddress_shouldReturnNoContent_whenNoForecastFound() throws Exception {
        // Arrange
        String ipAddress = "203.210.142.42";
        int currentHour = 9;

        Location locationFromIP = new Location();
        locationFromIP.setCode("LOC001");
        locationFromIP.setCityName("Hanoi");

        when(geolocationService.getLocation(ipAddress)).thenReturn(locationFromIP);
        when(hourlyWeatherService.getByLocation(locationFromIP, currentHour)).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get(END_POINT_PATH)
                        .header("X-Current-Hour", String.valueOf(currentHour))
                        .header("X-Forwarded-For", ipAddress)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listHourlyForecastByIPAddress_shouldReturnNotFound_whenLocationNotFound() throws Exception {
        // Arrange
        String ipAddress = "192.168.1.1";
        int currentHour = 10;

        when(geolocationService.getLocation(ipAddress)).thenThrow(new LocationNotFoundException("Location not found"));

        // Act & Assert
        mockMvc.perform(get(END_POINT_PATH)
                        .header("X-Current-Hour", String.valueOf(currentHour))
                        .header("X-Forwarded-For", ipAddress)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void listHourlyForecastByIPAddress_shouldReturnBadRequest_whenGeolocationExceptionOccurs() throws Exception {
        // Arrange
        String ipAddress = "192.168.1.1";
        int currentHour = 10;

        when(geolocationService.getLocation(ipAddress)).thenThrow(new GeolocationException("Geolocation error"));

        // Act & Assert
        mockMvc.perform(get(END_POINT_PATH)
                        .header("X-Current-Hour", String.valueOf(currentHour))
                        .header("X-Forwarded-For", ipAddress)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void listHourlyForecastByLocationCode_200OK() throws Exception {
        // Arrange
        String locationCode = "LOC001";
        int currentHour = 10;

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("Hanoi");
        location.setRegionName("Red River Delta");
        location.setCountryCode("VN");
        location.setCountryName("Viet nam");

        HourlyWeather hourlyWeather1 = new HourlyWeather()
                .location(location).hourOfDay(8)
                .temperature(30).precipitation(50)
                .status("Cloudy");
        HourlyWeather hourlyWeather2 = new HourlyWeather()
                .location(location).hourOfDay(9)
                .temperature(30).precipitation(50)
                .status("Sunny");

        List<HourlyWeather> hourlyForecasts = List.of(hourlyWeather1, hourlyWeather2);

        when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(hourlyForecasts);

        // Act & Assert
        mockMvc.perform(get(END_POINT_PATH + "/" + locationCode)
                        .header("X-Current-Hour", String.valueOf(currentHour))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value(location.toString()))
                .andExpect(jsonPath("$.hourlyForecast[0].hour_of_day").value(8))
                .andDo(print());
    }

    @Test
    void listHourlyForecastByLocationCode_shouldReturnNoContent_whenNoForecastFound() throws Exception {
        // Arrange
        String locationCode = "VN_HN";
        int currentHour = 10;

        when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get(END_POINT_PATH + "/" + locationCode)
                        .header("X-Current-Hour", String.valueOf(currentHour))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listHourlyForecastByLocationCode_shouldReturnNotFound_whenLocationNotFound() throws Exception {
        // Arrange
        String locationCode = "VN_UNKNOWN";
        int currentHour = 10;

        when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenThrow(new LocationNotFoundException("Location not found with the given code"));

        // Act & Assert
        mockMvc.perform(get(END_POINT_PATH + "/" + locationCode)
                        .header("X-Current-Hour", String.valueOf(currentHour))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void listHourlyForecastByLocationCode_shouldReturnBadRequest_whenHeaderInvalid() throws Exception {
        // Arrange
        String locationCode = "VN_HN";

        // Act & Assert
        mockMvc.perform(get(END_POINT_PATH + "/" + locationCode)
                        .header("X-Current-Hour", "invalid_hour")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
