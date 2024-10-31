package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
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

    public Location get(String code) {
        return locationRepository.findByCode(code);
    }

    public Location update(Location locationInRequest) throws LocationNotFoundException {
        String code = locationInRequest.getCode();

        Location locationInDB = locationRepository.findByCode(code);

        if (locationInDB == null) {
            throw new LocationNotFoundException("No location found with the given code :" + code);
        }

        locationInDB.setCityName(locationInRequest.getCityName());
        locationInDB.setRegionName(locationInRequest.getRegionName());
        locationInDB.setCountryName(locationInRequest.getCountryName());
        locationInDB.setCountryCode(locationInRequest.getCountryCode());
        locationInDB.setEnabled(locationInRequest.isEnabled());

        return locationRepository.save(locationInDB);
    }

    public void trashByCode(String code) throws LocationNotFoundException {
        if (!locationRepository.existsById(code)) {
            throw new LocationNotFoundException("No location found with the given code: " + code);
        }
        locationRepository.trashByCode(code);
    }
}
