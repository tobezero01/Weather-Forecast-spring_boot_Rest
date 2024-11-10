package com.skyapi.weatherforecast.clientApp;

import com.skyapi.weatherforecast.common.ClientApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientAppRepository extends JpaRepository<ClientApp, Integer> {

    @Query("SELECT c FROM ClientApp c WHERE c.clientId = ?1 AND c.enabled = true AND c.trashed = false")
    public Optional<ClientApp> findByClientId(String clientId);

}
