package com.skyapi.weatherforecast.daily;

import com.skyapi.weatherforecast.hourly.HourlyWeatherRepository;
import com.skyapi.weatherforecast.location.LocationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class DailyWeatherRepositoryTests {
    @Autowired
    private DailyWeatherRepository dailyWeatherRepository;
    @Autowired
    private LocationRepository locationRepository;

    @Test
    public void testAdd(){

    }
    @Test
    public void testDelete(){

    }

    // test findByLocationCode_NotFound

    // test findByLocationCode_Found


}
