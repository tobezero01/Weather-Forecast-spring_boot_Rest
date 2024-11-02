package com.skyapi.weatherforecast.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.WeatherApiServiceApplication;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.exception.GeolocationException;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationApiController;
import com.skyapi.weatherforecast.location.LocationService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RealtimeWeatherApiController.class)
public class RealtimeWeatherApiControllerTests {

    private static final String END_POINT_PATH = "/v1/realtime";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    RealtimeWeatherService realtimeWeatherService;
    @MockBean
    GeolocationService geolocationService;

    @Test
    void testGetRealtimeWeather_GeolocationException() throws Exception {
        // Mock the behavior of the geolocation service to throw an exception
        when(geolocationService.getLocation(Mockito.anyString())).thenThrow(GeolocationException.class);

        // Act & Assert
        mockMvc.perform(get(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRealtimeWeather_LocationNotFoundException() throws Exception {
        // Arrange
        String ipAddress = "";
        Location mockLocation = new Location();

        // Mock the behavior of the services
        when(geolocationService.getLocation(Mockito.anyString())).thenReturn(mockLocation);
        when(realtimeWeatherService.getByLocation(mockLocation)).thenThrow(LocationNotFoundException.class);

        // Simulate HttpServletRequest to provide IP address
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For".toUpperCase())).thenReturn(ipAddress);

        // Act & Assert
        mockMvc.perform(get(END_POINT_PATH)
                        .requestAttr("ipAddress", ipAddress)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetRealtimeWeather_Success() throws Exception {
        // Arrange
        String ipAddress = "192.168.1.1";
        Location mockLocation = new Location("LOC001", "Hanoi", "Hanoi", "Vietnam", "VN", true, false);
        RealtimeWeather mockWeather = new RealtimeWeather();
        mockWeather.setLocation(mockLocation);
        mockWeather.setLocationCode(mockLocation.getCode());
        mockWeather.setTemperature(25);
        mockWeather.setHumidity(70);
        mockWeather.setPrecipitation(0);
        mockWeather.setWindSpeed(15);
        mockWeather.setStatus("Clear");

        // Mock the behavior of the services
        when(geolocationService.getLocation(Mockito.anyString())).thenReturn(mockLocation);
        when(realtimeWeatherService.getByLocation(mockLocation)).thenReturn(mockWeather);

        // Simulate HttpServletRequest to provide IP address
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For".toUpperCase())).thenReturn(ipAddress);

        // Act & Assert
        mockMvc.perform(get(END_POINT_PATH)
                        .requestAttr("ipAddress", ipAddress)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.temperature").value(25))
                .andExpect(jsonPath("$.humidity").value(70))
                .andDo(print());
    }


    @Test
    void testGetRealtimeWeatherByLocationCode_Success() throws Exception {
        // Arrange
        String locationCode = "LOC001";
        RealtimeWeather mockWeather = new RealtimeWeather();
        mockWeather.setLocationCode(locationCode);
        mockWeather.setTemperature(25);
        mockWeather.setHumidity(70);
        mockWeather.setWindSpeed(15);
        mockWeather.setStatus("Clear");

        RealtimeWeatherDTO mockDTO = new RealtimeWeatherDTO();
        mockDTO.setLocation(locationCode);
        mockDTO.setTemperature(25);
        mockDTO.setHumidity(70);
        mockDTO.setWindSpeed(15);
        mockDTO.setStatus("Clear");

        when(realtimeWeatherService.getByLocationCode(locationCode)).thenReturn(mockWeather);

        // Act & Assert
        mockMvc.perform(get(END_POINT_PATH, locationCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temperature").value(25))
                .andExpect(jsonPath("$.humidity").value(70))
                .andExpect(jsonPath("$.windSpeed").value(15))
                .andExpect(jsonPath("$.status").value("Clear"))
                .andDo(print());
    }

    @Test
    void testGetRealtimeWeatherByLocationCode_NotFound() throws Exception {
        // Arrange
        String locationCode = "INVALID_CODE";

        when(realtimeWeatherService.getByLocationCode(locationCode)).thenThrow(new LocationNotFoundException("Location not found"));

        // Act & Assert
        mockMvc.perform(get(END_POINT_PATH, locationCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

}
