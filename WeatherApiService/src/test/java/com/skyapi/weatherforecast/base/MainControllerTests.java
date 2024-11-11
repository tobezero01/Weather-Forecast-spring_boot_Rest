package com.skyapi.weatherforecast.base;


import com.skyapi.weatherforecast.SecurityConfigForControllerTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(MainController.class)
@Import(SecurityConfigForControllerTests.class)
public class MainControllerTests {
    private static final String BASE_URI = "/";

    @Autowired private MockMvc mockMvc;

    @Test
    public void testBaseUri() throws Exception {
        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location_url").value("http://localhost/v1/locations"))
                .andExpect(jsonPath("$.location_by_code_url").value("http://localhost/v1/locations/{code}"))
                .andExpect(jsonPath("$.realtime_weather_by_ip_url").value("http://localhost/v1/realtime"))
                .andDo(print());
    }

}
