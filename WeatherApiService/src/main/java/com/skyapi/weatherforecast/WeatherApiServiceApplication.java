package com.skyapi.weatherforecast;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WeatherApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherApiServiceApplication.class, args);
	}
	
	@Bean
	CommandLineRunner  commandLineRunner() {
		return runner -> {
			System.out.println("Service started ...");
		};
	}

}
