package com.skyapi.weatherforecast.user;

import com.skyapi.weatherforecast.clientApp.ClientAppRepository;
import com.skyapi.weatherforecast.common.User;
import com.skyapi.weatherforecast.common.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {

    @Autowired private UserRepository userRepository;

    @Test
    public void testAddAdminUser() {
        User user = new User();
        user.setName("Nhu Dinh Duc");
        user.setEmail("ducnhuad@gmail.com");
        user.setEnabled(true);
        user.setType(UserType.ADMIN);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode("ducnhu1234"));

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
    }

    @Test
    public void testAddClientUser() {
        User user = new User();
        user.setName("Nhu Dinh Duc");
        user.setEmail("ducnhuad01@gmail.com");
        user.setEnabled(true);
        user.setType(UserType.CLIENT);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode("ducnhu1234"));

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
    }

}
