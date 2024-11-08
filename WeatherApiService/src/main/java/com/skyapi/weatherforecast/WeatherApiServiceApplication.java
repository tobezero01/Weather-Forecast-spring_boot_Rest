package com.skyapi.weatherforecast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.daily.DailyWeatherDTO;
import com.skyapi.weatherforecast.full.FullWeatherDTO;
import com.skyapi.weatherforecast.hourly.HourlyWeatherDTO;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Locale;

@SpringBootApplication
public class WeatherApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherApiServiceApplication.class, args);
	}

	@Bean
	public ModelMapper getModelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		var typeMap1 = mapper.typeMap(HourlyWeather.class, HourlyWeatherDTO.class);
		typeMap1.addMapping(src -> src.getId().getHourOfDay(), HourlyWeatherDTO::setHourOfDay);

		var typeMap2 = mapper.typeMap(HourlyWeatherDTO.class, HourlyWeather.class);
		typeMap2.addMapping(src -> src.getHourOfDay(), (dest, value) -> dest.getId().setHourOfDay(value != null ? (int) value : 0));

		var typeMap3 = mapper.typeMap(DailyWeather.class, DailyWeatherDTO.class);
		typeMap3.addMapping(src -> src.getId().getDayOfMonth(), DailyWeatherDTO :: setDayOfMonth);
		typeMap3.addMapping(src -> src.getId().getMonth(), DailyWeatherDTO :: setDayOfMonth);

		var typeMap4 = mapper.typeMap(DailyWeatherDTO.class, DailyWeather.class);
		typeMap4.addMapping(src -> src.getDayOfMonth(), (dest, value) -> dest.getId().setDayOfMonth(value != null ? (int) value : 0 ));
		typeMap4.addMapping(src -> src.getMonth(), (dest, value) -> dest.getId().setMonth(value != null ? (int) value : 0 ));

		var typeMap5 = mapper.typeMap(Location.class, FullWeatherDTO.class);
		typeMap5.addMapping(src -> src.toString(), FullWeatherDTO::setLocation);

		var typeMap6 = mapper.typeMap(RealtimeWeatherDTO.class, RealtimeWeather.class);
		typeMap6.addMappings(m -> m.skip(RealtimeWeather::setLocation));

		return mapper;
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();

		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

		return objectMapper;
	}
	@Bean
	CommandLineRunner  commandLineRunner() {
		return runner -> {
//			System.out.println("Welcome");
//			System.out.println("Service started ...");
		};
	}

}
