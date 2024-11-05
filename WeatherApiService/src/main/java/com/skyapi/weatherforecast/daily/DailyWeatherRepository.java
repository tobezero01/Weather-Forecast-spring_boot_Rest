package com.skyapi.weatherforecast.daily;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.DailyWeatherId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyWeatherRepository extends JpaRepository<DailyWeather, DailyWeatherId> {
}
