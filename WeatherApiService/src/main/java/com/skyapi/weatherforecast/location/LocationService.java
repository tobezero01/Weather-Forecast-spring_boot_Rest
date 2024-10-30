package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LocationService {

    private LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location add(Location location) {
        return locationRepository.save(location);
    }
    public boolean existsByCode(String code) {
        return locationRepository.existsById(code); // Hoặc sử dụng cách khác để kiểm tra mã địa điểm
    }
    public List<Location> list() {
        return locationRepository.findUnTrashed();
    }

}
