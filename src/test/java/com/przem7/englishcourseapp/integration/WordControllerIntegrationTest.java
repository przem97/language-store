package com.przem7.englishcourseapp.integration;

import com.przem7.englishcourseapp.model.dto.WordDTO;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase
class WordControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String SERVER_URL;

    @BeforeEach
    public void beforeEach() {
        SERVER_URL = "http://localhost:" + port;
    }

    @Test
    void getWordsShouldReturnEmptyArray() {
        // given
        String URL = SERVER_URL + "/words";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");

        // when
        ResponseEntity<WordDTO[]> exchange = testRestTemplate.exchange(URL,
                HttpMethod.GET,
                new HttpEntity<>(headers), WordDTO[].class
        );

        // then
        assertThat(exchange.getBody()).isNotNull();
        assertThat(exchange.getBody().length).isEqualTo(0);
    }
}
