package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

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

}
