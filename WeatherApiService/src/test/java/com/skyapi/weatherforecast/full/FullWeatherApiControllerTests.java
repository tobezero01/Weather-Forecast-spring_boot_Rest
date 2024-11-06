package com.skyapi.weatherforecast.full;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeolocationService;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.daily.DailyWeatherDTO;
import com.skyapi.weatherforecast.exception.GeolocationException;

import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import com.skyapi.weatherforecast.hourly.HourlyWeatherDTO;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FullWeatherApiController.class)
public class FullWeatherApiControllerTests {

    private static final String END_POINT_PATH = "/v1/full";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FullWeatherService fullWeatherService;

    @MockBean
    private GeolocationService geolocationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetByIPAddress_Return400BadRequest_GeolocationException() throws Exception {
        GeolocationException exception = new GeolocationException("Geolocation Error");
        when(geolocationService.getLocation(anyString())).thenThrow(exception);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Geolocation Error"))
                .andDo(print());
    }

    @Test
    public void testGetByIPAddress_Return404NotFound() throws Exception {
        Location location = new Location().code("LOC001");

        when(geolocationService.getLocation(anyString())).thenReturn(location);
        LocationNotFoundException ex = new LocationNotFoundException("Location not found");
        when(fullWeatherService.getByLocation(location)).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]").value("Location not found"))
                .andDo(print());
    }

    @Test
    public void testGetByIpAddress_return200OK() throws Exception {
        Location location = new Location();
        location.setCode("LOC001");
        location.setCityName("Hanoi");
        location.setRegionName("Red River Delta");
        location.setCountryCode("VN");
        location.setCountryName("Viet nam");

        RealtimeWeather mockWeather = new RealtimeWeather();
        mockWeather.setLocation(location);
        mockWeather.setLocationCode(location.getCode());
        mockWeather.setTemperature(25);
        mockWeather.setHumidity(70);
        mockWeather.setPrecipitation(0);
        mockWeather.setWindSpeed(15);
        mockWeather.setStatus("Clear");
        location.setRealtimeWeather(mockWeather);

        DailyWeather dailyWeather1 = new DailyWeather().minTemp(10)
                .maxTemp(20).location(location).precipitation(30)
                .status("Sunny").dayOfMonth(22).month(12);
        DailyWeather dailyWeather2 = new DailyWeather();
        dailyWeather2.setMinTemp(10);
        dailyWeather2.setMaxTemp(30);
        dailyWeather2.setPrecipitation(40);
        dailyWeather2.setStatus("Cloudy");
        dailyWeather2.location(location);
        dailyWeather2.dayOfMonth(23);
        dailyWeather2.month(12);
        location.setListDailyWeather(List.of(dailyWeather1, dailyWeather2));

        HourlyWeather hourlyWeather1 = new HourlyWeather()
                .location(location).hourOfDay(8)
                .temperature(30).precipitation(50)
                .status("Cloudy");
        HourlyWeather hourlyWeather2 = new HourlyWeather()
                .location(location).hourOfDay(9)
                .temperature(30).precipitation(50)
                .status("Sunny");
        List<HourlyWeather> hourlyForecasts = List.of(hourlyWeather1, hourlyWeather2);

        location.setListHourlyWeather(hourlyForecasts);

        when(geolocationService.getLocation(anyString())).thenReturn(location);
        when(fullWeatherService.getByLocation(location)).thenReturn(location);

        String locationToString = location.toString();

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location").value(locationToString))
                .andDo(print());
    }

    @Test
    public void testGetByLocationCode_Return404NotFound() throws Exception {
        String locationCode = "ADC";

        LocationNotFoundException ex = new LocationNotFoundException("Location not found");
        when(fullWeatherService.get(locationCode)).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH + "/" + locationCode))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]").value("Location not found"))
                .andDo(print());
    }

    @Test
    public void testGetByLocationCode_return200OK() throws Exception {
        Location location = new Location();
        location.setCode("LOC001");
        location.setCityName("Hanoi");
        location.setRegionName("Red River Delta");
        location.setCountryCode("VN");
        location.setCountryName("Viet nam");

        RealtimeWeather mockWeather = new RealtimeWeather();
        mockWeather.setLocation(location);
        mockWeather.setLocationCode(location.getCode());
        mockWeather.setTemperature(25);
        mockWeather.setHumidity(70);
        mockWeather.setPrecipitation(0);
        mockWeather.setWindSpeed(15);
        mockWeather.setStatus("Clear");
        location.setRealtimeWeather(mockWeather);

        DailyWeather dailyWeather1 = new DailyWeather().minTemp(10)
                .maxTemp(20).location(location).precipitation(30)
                .status("Sunny").dayOfMonth(22).month(12);
        DailyWeather dailyWeather2 = new DailyWeather();
        dailyWeather2.setMinTemp(10);
        dailyWeather2.setMaxTemp(30);
        dailyWeather2.setPrecipitation(40);
        dailyWeather2.setStatus("Cloudy");
        dailyWeather2.location(location);
        dailyWeather2.dayOfMonth(23);
        dailyWeather2.month(12);
        location.setListDailyWeather(List.of(dailyWeather1, dailyWeather2));

        HourlyWeather hourlyWeather1 = new HourlyWeather()
                .location(location).hourOfDay(8)
                .temperature(30).precipitation(50)
                .status("Cloudy");
        HourlyWeather hourlyWeather2 = new HourlyWeather()
                .location(location).hourOfDay(9)
                .temperature(30).precipitation(50)
                .status("Sunny");
        List<HourlyWeather> hourlyForecasts = List.of(hourlyWeather1, hourlyWeather2);

        location.setListHourlyWeather(hourlyForecasts);

        when(fullWeatherService.get(location.getCode())).thenReturn(location);

        String locationToString = location.toString();

        mockMvc.perform(get(END_POINT_PATH + "/" + location.getCode()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location").value(locationToString))
                .andDo(print());
    }


    @Test
    public void testUpdateByLocationCode_Return400BadRequest_NoDataHourlyWeather() throws Exception {
        String locationCode = "LOC004";
        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(get(END_POINT_PATH + "/" + locationCode)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Hourly weather data can not be empty"))
                .andDo(print());
    }

    @Test
    public void testUpdateByLocationCode_Return400BadRequest_NoDataDailyWeather() throws Exception {
        String locationCode = "LOC004";
        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();
        HourlyWeatherDTO hourlyWeather1 = new HourlyWeatherDTO()
                .hourOfDay(8)
                .temperature(30).precipitation(50)
                .status("Cloudy");

        fullWeatherDTO.getListHourlyWeather().add(hourlyWeather1);
        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(get(END_POINT_PATH + "/" + locationCode)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Daily weather data can not be empty"))
                .andDo(print());
    }

    @Test
    public void testUpdateByLocationCode_Return400BadRequest_InvalidRealtimeWeather() throws Exception {
        String locationCode = "LOC004";
        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();
        HourlyWeatherDTO hourlyWeather1 = new HourlyWeatherDTO()
                .hourOfDay(8)
                .temperature(30).precipitation(50)
                .status("Cloudy");
        DailyWeatherDTO dailyWeather1 = new DailyWeatherDTO().minTemp(10)
                .maxTemp(20).precipitation(30)
                .status("Sunny").dayOfMonth(22).month(12);

        RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
        realtimeDTO.setTemperature(255);
        realtimeDTO.setHumidity(708);
        realtimeDTO.setPrecipitation(0);
        realtimeDTO.setWindSpeed(15);
        realtimeDTO.setStatus("Clear");

        fullWeatherDTO.getListHourlyWeather().add(hourlyWeather1);
        fullWeatherDTO.getListDailyWeather().add(dailyWeather1);
        fullWeatherDTO.setRealtimeWeather(realtimeDTO);
        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        LocationNotFoundException ex = new LocationNotFoundException("Location not found with given code");
        when(fullWeatherService.update(Mockito.eq(locationCode), Mockito.any())).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH + "/" + locationCode)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    // test invalid hourly and daily
    @Test
    public void testUpdateByLocationCode_Return200OK() throws Exception {
        Location location = new Location();
        location.setCode("LOC001");
        location.setCityName("Hanoi");
        location.setRegionName("Red River Delta");
        location.setCountryCode("VN");
        location.setCountryName("Viet nam");
        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();
        HourlyWeatherDTO hourlyWeather1 = new HourlyWeatherDTO()
                .hourOfDay(8)
                .temperature(30).precipitation(50)
                .status("Cloudy");
        DailyWeatherDTO dailyWeather1 = new DailyWeatherDTO().minTemp(10)
                .maxTemp(20).precipitation(30)
                .status("Sunny").dayOfMonth(22).month(12);

        RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
        realtimeDTO.setTemperature(25);
        realtimeDTO.setHumidity(78);
        realtimeDTO.setPrecipitation(0);
        realtimeDTO.setWindSpeed(15);
        realtimeDTO.setStatus("Clear");

        fullWeatherDTO.getListHourlyWeather().add(hourlyWeather1);
        fullWeatherDTO.getListDailyWeather().add(dailyWeather1);
        fullWeatherDTO.setRealtimeWeather(realtimeDTO);
        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        when(fullWeatherService.update(Mockito.eq(location.getCode()), Mockito.any())).thenReturn(location);

        mockMvc.perform(get(END_POINT_PATH + "/" + location.getCode())
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
