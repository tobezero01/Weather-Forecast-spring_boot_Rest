package com.skyapi.weatherforecast;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import static org.apache.commons.lang3.compare.ComparableUtils.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {
    private static final String GET_ACCESS_TOKEN_ENDPOINT = "/oauth2/token";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAccessTokenFail() throws Exception {
        mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
                        .param("client_id", "...")
                        .param("client_secret", "...")
                        .param("grant_type", "client_credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("invalid_client"))
                .andDo(print());
    }

    @Test
    public void testGetAccessTokenSuccess() throws Exception {
        mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
                        .param("grant_type", "client_credentials")
                        .param("client_id", "ebdbf864-f7c8-47bb-9")
                        .param("client_secret", "d0ba5e46-82d1-4188-9461-97b19b85da33"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isString())
                .andExpect(jsonPath("$.expires_in").isNumber())
                .andDo(print());
    }

}
