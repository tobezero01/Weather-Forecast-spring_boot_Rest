package com.skyapi.weatherforecast.clientApp;

import com.skyapi.weatherforecast.common.*;
import com.skyapi.weatherforecast.location.LocationRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ClientAppRepositoryTests {

    @Autowired private ClientAppRepository clientAppRepository;

    @Test
    public void testFindByClientId() {
        String clientId = "ebdbf864-f7c8-47bb-9";
        Optional<ClientApp> res = clientAppRepository.findByClientId(clientId);

        assertThat(res).isNotNull();
    }

    @Test
    public void testAddSystemApp() {
        User user = new User(2);
        ClientApp clientApp = new ClientApp();
        clientApp.setRole(AppRole.SYSTEM);
        clientApp.setEnabled(true);
        clientApp.setName("Default client app");
        clientApp.setUser(user);

        ClientApp savedClient = clientAppRepository.save(clientApp);

        assertThat(savedClient).isNotNull();
    }

    @Test
    public void testAddUpdaterApp() {
        User user = new User(2);
        ClientApp clientApp = new ClientApp();
        clientApp.setRole(AppRole.UPDATER);
        clientApp.setEnabled(true);
        clientApp.setName("Default client app for updater");
        clientApp.setUser(user);

        ClientApp savedClient = clientAppRepository.save(clientApp);

        assertThat(savedClient).isNotNull();
    }
    @Test
    public void testAddReaderApp() {
        User user = new User(2);
        ClientApp clientApp = new ClientApp();
        clientApp.setRole(AppRole.READER);
        clientApp.setEnabled(true);
        clientApp.setName("Default client app for reader");
        clientApp.setUser(user);

        ClientApp savedClient = clientAppRepository.save(clientApp);

        assertThat(savedClient).isNotNull();
    }

}
