package com.skyapi.weatherforecast.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
                .andExpect(status().isOk())  // Kiểm tra mã trạng thái là 200 OK
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
        // Tạo dữ liệu giả cho location
        Location location = new Location();
        location.setCode("LOC001");
        location.setCityName("Hanoi");
        location.setRegionName("Red River Delta");
        location.setCountryName("Vietnam");
        location.setCountryCode("VN");
        location.setEnabled(true);
        location.setTrashed(false);

        // Giả lập behavior của locationService
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
        // Giả lập behavior trả về null nếu không tìm thấy location
        when(locationService.get("INVALID_CODE")).thenReturn(null);

        // Thực hiện GET request với mã code không tồn tại
        mockMvc.perform(get("/v1/locations/INVALID_CODE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Kiểm tra mã trạng thái là 404 Not Found
                .andDo(print()); // In response ra console
    }

}
