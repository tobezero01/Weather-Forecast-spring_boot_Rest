package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, String> , FilterableLocationRepository {

    @Query("SELECT l FROM Location l WHERE l.trashed = false")
    @Deprecated
    public List<Location> findUnTrashed();

    @Query("SELECT l FROM Location l WHERE l.trashed = false")
    @Deprecated
    public Page<Location> findUnTrashed(Pageable pageable);

    @Query("SELECT l FROM Location l WHERE l.trashed = false AND l.code = ?1")
    public Location findByCode(String code);

    @Modifying
    @Query("UPDATE Location l SET l.trashed = true WHERE l.code = ?1")
    void trashByCode(String code);

    @Query("SELECT l FROM Location l WHERE l.countryCode = ?1 AND l.cityName = ?2 AND l.trashed = false")
    public Location findByCountryCodeAndCityName(String countryCode, String cityName);
}
