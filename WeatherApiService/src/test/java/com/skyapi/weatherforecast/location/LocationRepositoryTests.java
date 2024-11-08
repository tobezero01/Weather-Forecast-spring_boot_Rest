package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class LocationRepositoryTests {

    @Autowired private LocationRepository locationRepository;

    @Test
    public void testAddSuccess() {
        // Tạo đối tượng Location mới
        Location location = new Location();
        location.setCode("NYC123");
        location.setCityName("New York");
        location.setRegionName("New York State");
        location.setCountryName("United States");
        location.setCountryCode("US");
        location.setEnabled(true);
        location.setTrashed(false);

        // Lưu đối tượng vào database
        Location savedLocation = locationRepository.save(location);

        // Sử dụng AssertJ để kiểm tra kết quả
        assertThat(savedLocation).isNotNull();
        assertThat(savedLocation.getCode()).isEqualTo("NYC123");
        assertThat(savedLocation.getCityName()).isEqualTo("New York");
    }

    @Test
    @Disabled
    public void testFindUnTrashed() {
        List<Location> unTrashedLocations = locationRepository.findUnTrashed();
        for (Location l : unTrashedLocations) {
            System.out.println(l);
        }
        // Kiểm tra kết quả
        assertThat(unTrashedLocations.size()).isGreaterThan(0);
    }

    @Test
    public void testFirstPageAndSort() {
        int pageSize = 5;
        int pageNum = 0;
        Sort sort = Sort.by("code").ascending();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        Page<Location> page = locationRepository.findUnTrashed(pageable);

        assertThat(page).size().isEqualTo(pageSize);
        page.forEach(System.out::println);
    }

    @Test
    public void testFindByCodeShouldReturnLocationWhenExists() {
        Location foundLocation = locationRepository.findByCode("LOC001");

        // Kiểm tra kết quả
        assertThat(foundLocation).isNotNull();
        assertThat(foundLocation.getCode()).isEqualTo("LOC001");
        assertThat(foundLocation.getCityName()).isEqualTo("Hanoi");
    }

    @Test
    public void testFindByCodeShouldReturnNullWhenCodeDoesNotExist() {
        // Gọi phương thức findByCode với code không tồn tại
        Location foundLocation = locationRepository.findByCode("NON_EXISTENT");

        // Kiểm tra kết quả là null
        assertThat(foundLocation).isNull();
    }

    @Test
    public void addRealtimeWeatherData() {
        String code = "LOC001"; // The location code to be tested
        Location location = locationRepository.findByCode(code);

        // Check if the location exists
        assertThat(location).isNotNull();

        // Get existing realtime weather or create a new one
        RealtimeWeather realtimeWeather = location.getRealtimeWeather();

        if (realtimeWeather == null) {
            realtimeWeather = new RealtimeWeather();
            realtimeWeather.setLocation(location);
            location.setRealtimeWeather(realtimeWeather); // Associate the new realtimeWeather with the location
        }

        // Update the realtime weather data
        realtimeWeather.setTemperature(25);
        realtimeWeather.setHumidity(70);
        realtimeWeather.setPrecipitation(0);
        realtimeWeather.setWindSpeed(15);
        realtimeWeather.setStatus("Clear");
        realtimeWeather.setLastUpdated(new Date());

        // Save the updated location, which should cascade the update to RealtimeWeather
        Location updatedLocation = locationRepository.save(location);

        // Assertions to verify the update was successful
        assertThat(updatedLocation.getRealtimeWeather()).isNotNull();
        assertThat(updatedLocation.getRealtimeWeather().getTemperature()).isEqualTo(25);
        assertThat(updatedLocation.getRealtimeWeather().getHumidity()).isEqualTo(70);
        assertThat(updatedLocation.getRealtimeWeather().getStatus()).isEqualTo("Clear");
    }


    // test for hourly

    @Test
    public void testAddHourlyWeatherData() {
        Location location = locationRepository.findById("LOC001").get();
        List<HourlyWeather> hourlyWeatherList = location.getListHourlyWeather();

        HourlyWeather forecast1 = new HourlyWeather().id(location, 8)
                .temperature(20).precipitation(60).status("Sunny");
        HourlyWeather forecast2 = new HourlyWeather().location(location).hourOfDay(10)
                .temperature(25).precipitation(10).status("Rainy");

        hourlyWeatherList.add(forecast1);
        hourlyWeatherList.add(forecast2);

        Location updatedLocation = locationRepository.save(location);
        assertThat(updatedLocation.getListHourlyWeather()).isNotEmpty();
    }

    @Test
    public void testFindByCountryCodeAndCityName_NotFound() {
        String countryCode = "US";
        String cityName = "New York City";

        Location location = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);

        assertThat(location).isNull();
    }

    @Test
    public void testFindByCountryCodeAndCityName_Found() {
        String countryCode = "LOC004";
        String cityName = "Nha Trang";

        Location location = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);

        assertThat(location).isNotNull();
        assertThat(location.getCountryCode()).isEqualTo("LOC004");
    }


    // test for
    @Test
    public void testAddDailyWeather() {
        Location location = locationRepository.findById("LOC001").get();
        List<DailyWeather> list = location.getListDailyWeather();

        DailyWeather dailyWeather1 = new DailyWeather().location(location)
                .dayOfMonth(17).month(7).minTemp(20).maxTemp(30)
                .precipitation(30).status("Sunny");
        DailyWeather dailyWeather2 = new DailyWeather().location(location)
                .dayOfMonth(18).month(7).minTemp(20).maxTemp(30)
                .precipitation(20).status("Cloudy");

        list.add(dailyWeather1);list.add(dailyWeather2);
        Location updatedLocation = locationRepository.save(location);
        assertThat(updatedLocation.getListDailyWeather()).isNotEmpty();
    }

}
