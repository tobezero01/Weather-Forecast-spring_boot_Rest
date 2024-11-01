package com.skyapi.weatherforecast;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import static org.assertj.core.api.Assertions.*;

public class IP2LocationTests {

    // information https://github.com/ip2location/ip2location-java
    private String DBPath = "ip2locdb/IP2LOCATION-LITE-DB3.BIN";

    public IP2Location ip2Location;

    @BeforeEach
    public void startPoint() throws IOException {
        ip2Location = new IP2Location();
        ip2Location.Open(DBPath);
    }

    @Test
    public void testInvalidIP() throws IOException {
        String idAddress = "abc";
        IPResult ipResult = ip2Location.IPQuery(idAddress);
        assertThat(ipResult.getStatus()).isEqualTo("INVALID_IP_ADDRESS");

        System.out.println(ipResult);
    }

    @Test
    public void testValidIP() throws IOException {
        String idAddress = "171.244.32.220";
        IPResult ipResult = ip2Location.IPQuery(idAddress);
        assertThat(ipResult.getStatus()).isEqualTo("OK");
        assertThat(ipResult.getCity()).isEqualTo("Hanoi");
        System.out.println(ipResult);
    }
}
