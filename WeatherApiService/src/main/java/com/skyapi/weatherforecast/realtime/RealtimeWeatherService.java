package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import com.skyapi.weatherforecast.location.LocationService;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class RealtimeWeatherService {
    private RealtimeWeatherRepository realtimeWeatherRepository;
    private LocationRepository locationRepository;

    public RealtimeWeatherService(RealtimeWeatherRepository realtimeWeatherRepository,
                                  LocationRepository locationRepository) {
        this.realtimeWeatherRepository = realtimeWeatherRepository;
        this.locationRepository = locationRepository;
    }

    public RealtimeWeather getByLocation(Location location) throws LocationNotFoundException {
        String countryCode = location.getCountryCode();
        String city = location.getCityName();

        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByCountryCodeAndCity(countryCode, city);
        if (realtimeWeather == null) {
            throw new LocationNotFoundException("No location found with the given country and city");
        }

        return realtimeWeather;
    }

    public RealtimeWeather getByLocationCode(String code) throws LocationNotFoundException {
        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByLocationCode(code);

        if (realtimeWeather == null) {
            throw new LocationNotFoundException("No location found with the given locationCode : " + code);
        }
        return realtimeWeather;
    }

    public RealtimeWeather update(String locationCode, RealtimeWeather updatedRealtimeWeather) throws LocationNotFoundException {
        // Lấy đối tượng Location để kiểm tra xem nó có tồn tại không
        Location location = locationRepository.findByCode(locationCode);

        if (location == null) {
            throw new LocationNotFoundException("Không tìm thấy vị trí với mã locationCode: " + locationCode);
        }

        // Kiểm tra xem đã có RealtimeWeather nào liên kết với Location này chưa
        RealtimeWeather existingWeather = location.getRealtimeWeather();
        if (existingWeather != null) {
            // Cập nhật các trường của đối tượng đang được quản lý (managed entity)
            existingWeather.setTemperature(updatedRealtimeWeather.getTemperature());
            existingWeather.setHumidity(updatedRealtimeWeather.getHumidity());
            existingWeather.setPrecipitation(updatedRealtimeWeather.getPrecipitation());
            existingWeather.setWindSpeed(updatedRealtimeWeather.getWindSpeed());
            existingWeather.setStatus(updatedRealtimeWeather.getStatus());
            existingWeather.setLastUpdated(new Date());

            return realtimeWeatherRepository.save(existingWeather);
        } else {
            // Nếu chưa có RealtimeWeather nào, thiết lập đối tượng mới và lưu
            updatedRealtimeWeather.setLocation(location);
            updatedRealtimeWeather.setLastUpdated(new Date());
            location.setRealtimeWeather(updatedRealtimeWeather);

            locationRepository.save(location);  // Lưu Location sẽ tự động lưu RealtimeWeather nếu đã thiết lập Cascade
            return updatedRealtimeWeather;
        }
    }





}
