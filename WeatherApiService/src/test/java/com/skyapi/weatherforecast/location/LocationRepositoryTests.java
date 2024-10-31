package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

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
    public void testFindUnTrashed() {
        List<Location> unTrashedLocations = locationRepository.findUnTrashed();
        for (Location l : unTrashedLocations) {
            System.out.println(l);
        }
        // Kiểm tra kết quả
        assertThat(unTrashedLocations.size()).isGreaterThan(0);
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


}
