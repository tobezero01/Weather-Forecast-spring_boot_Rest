package com.skyapi.weatherforecast.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationApiController.class)
public class LocationApiControllerTests {

    private static final String END_POINT_PATH = "/v1/locations";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    LocationService locationService;

    @Test
    public void testAddShouldReturn400BadRequest() throws Exception {
        Location location = new Location(); // Đối tượng Location trống

        String bodyContent = mapper.writeValueAsString(location);

        // Mô phỏng phương thức existsByCode để trả về false
        // (nếu cần, bạn có thể thêm các giá trị khác vào đây)
        when(locationService.existsByCode(anyString())).thenReturn(false);

        mockMvc.perform(post(END_POINT_PATH)
                        .contentType("application/json")
                        .content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testAddShouldReturn201Created() throws Exception {
        // Tạo một đối tượng Location hợp lệ
        Location location = new Location();
        location.setCode("LOC001");
        location.setCityName("Hanoi");
        location.setRegionName("Red River Delta");
        location.setCountryName("Vietnam");
        location.setCountryCode("VN");
        location.setEnabled(true);
        location.setTrashed(false);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post("/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyContent))
                .andExpect(status().isCreated()) // Kiểm tra mã trạng thái là 201 Created
                .andDo(print());
    }

    @Test
    public void testListLocationsShouldReturn200WithData() throws Exception {
        // Tạo danh sách Location mẫu
        Location location1 = new Location("LOC001", "Hanoi", "Red River Delta", "Vietnam", "VN", true, false);
        Location location2 = new Location("LOC002", "Ho Chi Minh City", "Southeast", "Vietnam", "VN", true, false);

        List<Location> locations = Arrays.asList(location1, location2);

        // Giả lập kết quả trả về của locationService.list()
        Mockito.when(locationService.list()).thenReturn(locations);

        // Thực hiện yêu cầu GET và kiểm tra kết quả
        mockMvc.perform(get("/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))  // Kiểm tra có 2 phần tử trong danh sách
                .andExpect(jsonPath("$[0].code").value("LOC001"))  // Kiểm tra mã của phần tử đầu tiên
                .andExpect(jsonPath("$[1].code").value("LOC002"))  // Kiểm tra mã của phần tử thứ hai
                .andDo(print());
    }

    @Test
    public void testListLocationsShouldReturn204WhenEmpty() throws Exception {
        // Giả lập kết quả trả về là danh sách rỗng
        Mockito.when(locationService.list()).thenReturn(Collections.emptyList());

        // Thực hiện yêu cầu GET và kiểm tra kết quả
        mockMvc.perform(get("/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())  // Kiểm tra mã trạng thái là 204 No Content
                .andDo(print());
    }


    @Test
    public void testGetLocationByCode_ShouldReturn200WhenFound() throws Exception {
        Location location = new Location();
        location.setCode("LOC001");
        location.setCityName("Hanoi");
        location.setRegionName("Red River Delta");
        location.setCountryName("Vietnam");
        location.setCountryCode("VN");
        location.setEnabled(true);
        location.setTrashed(false);

        when(locationService.get("LOC001")).thenReturn(location);

        mockMvc.perform(get("/v1/locations/LOC001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Kiểm tra mã trạng thái là 200 OK
                .andExpect(jsonPath("$.code").value("LOC001")) // Kiểm tra code
                .andExpect(jsonPath("$.cityName").value("Hanoi")) // Kiểm tra cityName
                .andDo(print()); // In response ra console
    }

    @Test
    public void testGetLocationByCode_ShouldReturn404WhenNotFound() throws Exception {
        when(locationService.get("INVALID_CODE")).thenReturn(null);

        mockMvc.perform(get("/v1/locations/INVALID_CODE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Kiểm tra mã trạng thái là 404 Not Found
                .andDo(print()); // In response ra console
    }


    @Test
    public void testUpdateLocation_ShouldReturn200WhenSuccessful() throws Exception {
        Location locationInDB = new Location();
        locationInDB.setCode("LOC001");
        locationInDB.setCityName("Hanoi");
        locationInDB.setRegionName("Red River Delta");
        locationInDB.setCountryName("Vietnam");
        locationInDB.setCountryCode("VN");
        locationInDB.setEnabled(true);

        Location locationRequest = new Location();
        locationRequest.setCityName("Ho Chi Minh City");
        locationRequest.setRegionName("South");
        locationRequest.setCountryName("Vietnam");
        locationRequest.setCountryCode("VN");
        locationRequest.setEnabled(false);

        when(locationService.update(any(Location.class))).thenReturn(locationRequest);

        String requestBody = mapper.writeValueAsString(locationRequest);

        mockMvc.perform(put("/v1/locations/LOC001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cityName").value("Ho Chi Minh City"))
                .andExpect(jsonPath("$.regionName").value("South"))
                .andExpect(jsonPath("$.enabled").value(false))
                .andDo(print());
    }

    @Test
    public void testUpdateLocation_ShouldReturn404WhenNotFound() throws Exception {
        doThrow(new LocationNotFoundException("No location found with the given code: LOC999"))
                .when(locationService).update(any(Location.class));
        Location locationRequest = new Location();
        locationRequest.setCityName("Unknown City");

        String requestBody = mapper.writeValueAsString(locationRequest);
        mockMvc.perform(put("/v1/locations/LOC999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testTrashLocation_ShouldReturn204WhenSuccessful() throws Exception {
        Location locationInDB = new Location();
        locationInDB.setCode("LOC001");
        locationInDB.setCityName("Hanoi");
        locationInDB.setRegionName("Red River Delta");
        locationInDB.setCountryName("Vietnam");
        locationInDB.setCountryCode("VN");
        locationInDB.setEnabled(true);

        mockMvc.perform(delete("/v1/locations/LOC001"))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(locationService, times(1)).trashByCode("LOC001");
    }
    @Test
    public void testTrashLocation_ShouldReturn404WhenNotFound() throws Exception {
        doThrow(new LocationNotFoundException("No location found with the given code: LOC999"))
                .when(locationService).trashByCode("LOC999");

        mockMvc.perform(delete("/v1/locations/LOC999/trash"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }


    // TEst validation
    @Test
    public void testAddShouldReturn400ForBlankCode() throws Exception {
        Location location = new Location();
        location.setCode(""); // Blank code, should trigger validation error
        location.setCityName("Hanoi");
        location.setRegionName("Red River Delta");
        location.setCountryName("Vietnam");
        location.setCountryCode("VN");

        String bodyContent = mapper.writeValueAsString(location);

        when(locationService.existsByCode(anyString())).thenReturn(false);

        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").value("Code is required and cannot be blank"))
                .andDo(print());
    }

    @Test
    public void testAddShouldReturn400ForBlankCityName() throws Exception {
        Location location = new Location();
        location.setCode("LOC001");
        location.setCityName(""); // Blank city name
        location.setRegionName("Red River Delta");
        location.setCountryName("Vietnam");
        location.setCountryCode("VN");

        String bodyContent = mapper.writeValueAsString(location);

        when(locationService.existsByCode(anyString())).thenReturn(false);

        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("City name is required and cannot be blank"))
                .andDo(print());
    }

    @Test
    public void testAddShouldReturn400ForNullRegionName() throws Exception {
        Location location = new Location();
        location.setCode("LOC002");
        location.setCityName("Ho Chi Minh City");
        location.setRegionName(null); // Null region name, should trigger validation error
        location.setCountryName("Vietnam");
        location.setCountryCode("VN");

        String bodyContent = mapper.writeValueAsString(location);

        when(locationService.existsByCode(anyString())).thenReturn(false);

        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Region name cannot be null"))
                .andDo(print());
    }

    @Test
    public void testAddShouldReturn400ForBlankCountryName() throws Exception {
        Location location = new Location();
        location.setCode("LOC003");
        location.setCityName("Hue");
        location.setRegionName("Central");
        location.setCountryName(""); // Blank country name
        location.setCountryCode("VN");

        String bodyContent = mapper.writeValueAsString(location);

        when(locationService.existsByCode(anyString())).thenReturn(false);

        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Country name is required and cannot be blank"))
                .andDo(print());
    }

    @Test
    public void testAddShouldReturn400ForBlankCountryCode() throws Exception {
        Location location = new Location();
        location.setCode("LOC004");
        location.setCityName("Da Nang");
        location.setRegionName("Central Coast");
        location.setCountryName("Vietnam");
        location.setCountryCode(""); // Blank country code

        String bodyContent = mapper.writeValueAsString(location);

        when(locationService.existsByCode(anyString())).thenReturn(false);

        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Country code is required and cannot be blank"))
                .andDo(print());
    }


    @Test
    public void testAddShouldReturn400ForLongCode() throws Exception {
        Location location = new Location();
        location.setCode("LOC1234567890"); // Exceeds 12 characters
        location.setCityName("Hanoi");
        location.setRegionName("Red River Delta");
        location.setCountryName("Vietnam");
        location.setCountryCode("VN");

        String bodyContent = mapper.writeValueAsString(location);

        when(locationService.existsByCode(anyString())).thenReturn(false);

        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Code cannot be longer than 12 characters"))
                .andDo(print());
    }

    @Test
    public void testAddShouldReturn400ForLongCityName() throws Exception {
        Location location = new Location();
        location.setCode("LOC001");
        location.setCityName("A very long city name that exceeds the maximum allowed 128 characters for the field validation to trigger in the test case setup.");
        location.setRegionName("Red River Delta");
        location.setCountryName("Vietnam");
        location.setCountryCode("VN");

        String bodyContent = mapper.writeValueAsString(location);

        when(locationService.existsByCode(anyString())).thenReturn(false);

        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("City name cannot be longer than 128 characters"))
                .andDo(print());
    }

    @Test
    public void testAddShouldReturn400ForInvalidCountryCodeLength() throws Exception {
        Location location = new Location();
        location.setCode("LOC003");
        location.setCityName("Hue");
        location.setRegionName("Central");
        location.setCountryName("Vietnam");
        location.setCountryCode("VN12"); // Exceeds 2 characters

        String bodyContent = mapper.writeValueAsString(location);

        when(locationService.existsByCode(anyString())).thenReturn(false);

        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Country code must be exactly 2 ỏ  characters"))
                .andDo(print());
    }

}
