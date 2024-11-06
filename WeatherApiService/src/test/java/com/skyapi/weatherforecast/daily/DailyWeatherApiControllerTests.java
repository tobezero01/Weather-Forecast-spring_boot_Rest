package com.skyapi.weatherforecast.daily;

import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.exception.GeolocationException;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DailyWeatherApiController.class)
public class DailyWeatherApiControllerTests {

    private static final String END_POINT_PATH = "/v1/daily";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DailyWeatherService dailyWeatherService;

    @MockBean
    private GeolocationService geolocationService;

    @MockBean
    private ModelMapper modelMapper;

    @Test
    public void testGetByIpAddress_return400_GeolocationException() throws Exception {
        GeolocationException exception = new GeolocationException("Error");
        when(geolocationService.getLocation(anyString())).thenThrow(exception);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Error"))
                .andDo(print());
    }

    @Test
    public void testGetByIpAddress_returnNotFound() throws Exception {
        Location location = new Location().code("LOC001");
        when(geolocationService.getLocation(anyString())).thenReturn(location);

        LocationNotFoundException ex = new LocationNotFoundException("Location not found with the given code");
        when(dailyWeatherService.getByLocation(location)).thenThrow(ex);
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]").value("Location not found with the given code"))
                .andDo(print());
    }

    @Test
    public void testGetByIpAddress_return204NoContent() throws Exception {
        Location location = new Location().code("LOC001");
        when(geolocationService.getLocation(anyString())).thenReturn(location);

        when(dailyWeatherService.getByLocation(location)).thenReturn(new ArrayList<>());
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNoContent())
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

        DailyWeather dailyWeather = new DailyWeather().minTemp(10)
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

        location.setListDailyWeather(List.of(dailyWeather, dailyWeather2));
        when(geolocationService.getLocation(anyString())).thenReturn(location);
        when(dailyWeatherService.getByLocation(location)).thenReturn(List.of(dailyWeather, dailyWeather2));

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.daily_forecast[0].day_of_month").value(22))
                .andDo(print());
    }

    @Test
    public void testGetByCode_return200OK() throws Exception {
        String locationCode = "LOC001";

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("Hanoi");
        location.setRegionName("Red River Delta");
        location.setCountryCode("VN");
        location.setCountryName("Vietnam");

        DailyWeather dailyWeather = new DailyWeather().minTemp(10)
                .maxTemp(20).location(location).precipitation(30)
                .status("Sunny").dayOfMonth(22).month(12);

        DailyWeather dailyWeather2 = new DailyWeather().minTemp(15)
                .maxTemp(25).location(location).precipitation(40)
                .status("Cloudy").dayOfMonth(23).month(12);

        location.setListDailyWeather(List.of(dailyWeather, dailyWeather2));

        when(dailyWeatherService.getByLocationCode(locationCode)).thenReturn(List.of(dailyWeather, dailyWeather2));

        mockMvc.perform(get(END_POINT_PATH + "/" + locationCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.daily_forecast[0].day_of_month").value(22))
                .andExpect(jsonPath("$.daily_forecast[1].status").value("Cloudy"))
                .andDo(print());
    }

}
