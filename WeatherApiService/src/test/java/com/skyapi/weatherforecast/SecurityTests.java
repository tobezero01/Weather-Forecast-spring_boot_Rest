package com.skyapi.weatherforecast;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {
    private static final String GET_ACCESS_TOKEN_ENDPOINT = "/oauth2/token";

    @MockBean
    MockMvc mockMvc;
    @Test
    public void testGetAccessTokenFail() throws Exception {
        mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
                .param("client_id", "abs")
                .param("client_secret", "def")
                .param("grant_type", "client_credentials")
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("invalid_client"));

    }

    @Test
    public void testGetAccessTokenSuccess() throws Exception {
        mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
                        .param("client_id", "abs")
                        .param("client_secret", "def")
                        .param("grant_type", "client_credentials")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isString())
                .andExpect(jsonPath("$.expires_in").isNumber())
                .andExpect(jsonPath("$.token_type").value("Bearer"))

        ;
    }
}
