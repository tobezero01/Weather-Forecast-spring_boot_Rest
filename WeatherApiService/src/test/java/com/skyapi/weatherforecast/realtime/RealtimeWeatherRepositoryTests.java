package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.*;

import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RealtimeWeatherRepositoryTests {
    @Autowired
    private RealtimeWeatherRepository realtimeWeatherRepository;
    @Autowired
    private LocationRepository locationRepository;

    @Test
    public void updateTest() {
        String locationCode = "LOC001";
        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findById(locationCode).get();

        realtimeWeather.setTemperature(30);
        realtimeWeather.setHumidity(50);
        realtimeWeather.setWindSpeed(15);
        realtimeWeather.setStatus("Sunny");
        realtimeWeather.setLastUpdated(new Date());

        RealtimeWeather realtimeWeatherBefore = realtimeWeatherRepository.save(realtimeWeather);
        assertThat(realtimeWeatherBefore).isNotNull();
        assertThat(realtimeWeatherBefore.getTemperature()).isEqualTo(30);
        assertThat(realtimeWeatherBefore.getHumidity()).isEqualTo(50);
    }

    @Test
    void testFindByCountryCodeAndCity_Ok() {
        RealtimeWeather foundWeather = realtimeWeatherRepository.findByCountryCodeAndCity("VN", "Hanoi");

        assertThat(foundWeather).isNotNull();
        assertThat(foundWeather.getLocationCode()).isEqualTo("LOC001");
        assertThat(foundWeather.getTemperature()).isEqualTo(25);
    }

    @Test
    void testFindByCountryCodeAndCity_NotFound() {
        RealtimeWeather foundWeather = realtimeWeatherRepository.findByCountryCodeAndCity("VN", "Ho Chi Minh City");

        assertThat(foundWeather).isNull();
    }
}
