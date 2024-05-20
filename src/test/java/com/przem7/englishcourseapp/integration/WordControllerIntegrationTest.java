package com.przem7.englishcourseapp.integration;

import com.przem7.englishcourseapp.controller.factory.WordFactory;
import com.przem7.englishcourseapp.mapper.WordMapper;
import com.przem7.englishcourseapp.model.dto.WordDTO;
import com.przem7.englishcourseapp.model.orm.Word;
import com.przem7.englishcourseapp.repository.WordRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
class WordControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private WordMapper wordMapper;

    @Autowired
    @Qualifier("dtoDateFormat")
    DateTimeFormatter format;

    private BasicJsonTester json = new BasicJsonTester(getClass());

    private String SERVER_URL;

    @BeforeEach
    public void beforeEach() {
        SERVER_URL = "http://localhost:" + port;
        wordRepository.deleteAll();
    }

    @Test
    void getWordsShouldReturnEmptyArray() {
        // given
        String URL = SERVER_URL + "/words";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<WordDTO[]> exchange = testRestTemplate.exchange(URL,
                HttpMethod.GET,
                new HttpEntity<>(headers), WordDTO[].class
        );

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(exchange.getBody()).isNotNull();
        assertThat(exchange.getBody()).isInstanceOf(Object[].class);
        assertThat(exchange.getBody().length).isEqualTo(0);
    }

    @Test
    void getWordsShouldReturnArrayOfTwoWords() {
        // given
        LocalDateTime initialDate = LocalDateTime.now();
        List<Word> words = WordFactory.createWords(2);
        wordRepository.saveAll(words);

        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<WordDTO[]> exchange = testRestTemplate.exchange(uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), WordDTO[].class
        );

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(exchange.getBody()).isNotNull()
                .hasSize(2);
        for (Word word : words) {
            assertThat(exchange.getBody())
                    .filteredOn(x -> x.getId().equals(word.getId()))
                    .isNotNull()
                    .hasSize(1)
                    .areExactly(1, allOf(
                            new Condition<>(w -> w.getId().equals(word.getId()), "Id should equal to '%d'", word.getId()),
                            new Condition<>(w -> w.getValue().equals(word.getValue()), "Value should equal to '%s'", word.getValue()),
                            new Condition<>(w -> w.getLanguage().equals(word.getLanguage()), "Language should equal to '%s'", word.getLanguage().name()),
                            new Condition<>(w -> w.getCreated().isAfter(initialDate), "Created should be after '%s'", initialDate.toString())
                    ));
        }
    }

    @Test
    void getWordsShouldFilterByContaining() {
        // given
        LocalDateTime initialDate = LocalDateTime.now();
        Word word1 = WordFactory.create();
        Word word2 = WordFactory.create();
        Word word3 = WordFactory.create();
        word1.setValue("perky");
        word2.setValue("perk");
        word3.setValue("dog");
        wordRepository.save(word1);
        wordRepository.save(word2);
        wordRepository.save(word3);

        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .queryParam("containing", "perk")
                .build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<WordDTO[]> exchange = testRestTemplate.exchange(uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), WordDTO[].class
        );

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(exchange.getBody())
                .isNotNull()
                .hasSize(2);
        for (Word word : List.of(word1, word2)) {
            assertThat(exchange.getBody())
                    .filteredOn(x -> x.getId().equals(word.getId()))
                    .isNotNull()
                    .hasSize(1)
                    .areExactly(1, allOf(
                            new Condition<>(w -> w.getId().equals(word.getId()), "Id should equal to '%d'", word.getId()),
                            new Condition<>(w -> w.getValue().equals(word.getValue()), "Value should equal to '%s'", word.getValue()),
                            new Condition<>(w -> w.getLanguage().equals(word.getLanguage()), "Language should equal to '%s'", word.getLanguage().name()),
                            new Condition<>(w -> w.getCreated().isAfter(initialDate), "Created should be after '%s'", initialDate.toString())
                    ));
        }
    }

    @Test
    void getWordsShouldReturn3ConsecutivePagesWhereFirst2ConsistingOf2WordsAndLastOneConsistingOf1Word() {
        // given

        // initialize database with 5 words
        wordRepository.saveAll(WordFactory.createWords(5));

        URI firstPageUri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .queryParam("pageSize", "2")
                .queryParam("pageNumber", "0")
                .build().toUri();
        URI secondPageUri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .queryParam("pageSize", "2")
                .queryParam("pageNumber", "1")
                .build().toUri();
        URI thirdPageUri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .queryParam("pageSize", "2")
                .queryParam("pageNumber", "2")
                .build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<WordDTO[]> firstPage = testRestTemplate.exchange(firstPageUri,
                HttpMethod.GET,
                new HttpEntity<>(headers), WordDTO[].class
        );
        ResponseEntity<WordDTO[]> secondPage = testRestTemplate.exchange(secondPageUri,
                HttpMethod.GET,
                new HttpEntity<>(headers), WordDTO[].class
        );
        ResponseEntity<WordDTO[]> thirdPage = testRestTemplate.exchange(thirdPageUri,
                HttpMethod.GET,
                new HttpEntity<>(headers), WordDTO[].class
        );

        // then
        assertThat(firstPage.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(firstPage.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(secondPage.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(secondPage.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(thirdPage.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(thirdPage.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(firstPage.getBody())
                .isNotNull()
                .hasSize(2);
        assertThat(secondPage.getBody())
                .isNotNull()
                .hasSize(2);
        assertThat(thirdPage.getBody())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void getWordsShouldSortByIdByDefault() {
        // given

        // initialize database with 7 words
        wordRepository.saveAll(WordFactory.createWords(7));

        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .queryParam("pageSize", "10")
                .queryParam("pageNumber", "0")
                .build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<WordDTO[]> exchange = testRestTemplate.exchange(uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), WordDTO[].class
        );

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(exchange.getBody())
                .isNotNull()
                .hasSize(7);

        assertThat(exchange.getBody())
                .isSortedAccordingTo(Comparator.comparingLong(WordDTO::getId));
    }

    @Test
    void getWordsShouldSortById() {
        // given

        // initialize database with 7 words
        wordRepository.saveAll(WordFactory.createWords(7));

        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .queryParam("pageSize", "10")
                .queryParam("pageNumber", "0")
                .queryParam("sortBy", "id")
                .build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<WordDTO[]> exchange = testRestTemplate.exchange(uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), WordDTO[].class
        );

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(exchange.getBody())
                .isNotNull()
                .hasSize(7);

        assertThat(exchange.getBody())
                .isSortedAccordingTo(Comparator.comparingLong(WordDTO::getId));
    }

    @Test
    void getWordsShouldSortByValue() {
        // given

        // initialize database with 7 words
        wordRepository.saveAll(WordFactory.createWords(7));

        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .queryParam("pageSize", "10")
                .queryParam("pageNumber", "0")
                .queryParam("sortBy", "value")
                .build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<WordDTO[]> exchange = testRestTemplate.exchange(uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), WordDTO[].class
        );

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(exchange.getBody())
                .isNotNull()
                .hasSize(7);

        assertThat(exchange.getBody())
                .isSortedAccordingTo(Comparator.comparing(WordDTO::getValue));
    }

    @Test
    void getWordsShouldSortByTimeCreated() {
        // given

        // initialize database with 7 words
        wordRepository.saveAll(WordFactory.createWords(7));

        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .queryParam("pageSize", "10")
                .queryParam("pageNumber", "0")
                .queryParam("sortBy", "created")
                .build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<WordDTO[]> exchange = testRestTemplate.exchange(uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), WordDTO[].class
        );

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        assertThat(exchange.getBody())
                .isNotNull()
                .hasSize(7);

        assertThat(exchange.getBody())
                .isSortedAccordingTo(Comparator.comparing(WordDTO::getCreated));
    }

    @Test
    void getWordsShouldReturnValidationFailureOnNegativePageNumberGiven() {
        // given
        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .queryParam("pageNumber", "-2")
                .build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<String> exchange = testRestTemplate.exchange(uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), String.class
        );

        JsonContent<Object> jsonContent = json.from(exchange.getBody());

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(jsonContent).isNotNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.type").isEqualToIgnoringCase("about:blank");
        assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualToIgnoringCase("OK");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.status").isEqualTo(200);
        assertThat(jsonContent).extractingJsonPathStringValue("$.detail").isEqualToIgnoringCase("Validation failure");
        assertThat(jsonContent).extractingJsonPathStringValue("$.instance").isEqualToIgnoringCase("/words");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.pageNumber[0].message").isEqualToIgnoringCase("must be greater than or equal to 0");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.pageNumber[0].value").isEqualToIgnoringCase("-2");
    }

    @Test
    void getWordsShouldReturnValidationFailureOnNegativePageSizeGiven() {
        // given
        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .queryParam("pageSize", "-9")
                .build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<String> exchange = testRestTemplate.exchange(uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), String.class
        );

        JsonContent<Object> jsonContent = json.from(exchange.getBody());

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(jsonContent).isNotNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.type").isEqualToIgnoringCase("about:blank");
        assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualToIgnoringCase("OK");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.status").isEqualTo(200);
        assertThat(jsonContent).extractingJsonPathStringValue("$.detail").isEqualToIgnoringCase("Validation failure");
        assertThat(jsonContent).extractingJsonPathStringValue("$.instance").isEqualToIgnoringCase("/words");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.pageSize[0].message").isEqualToIgnoringCase("must be greater than 0");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.pageSize[0].value").isEqualToIgnoringCase("-9");
    }

    @Test
    void getWordsShouldReturnValidationFailureOnNegativePageSizeAndPageNumberGiven() {
        // given
        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .queryParam("pageNumber", "-51")
                .queryParam("pageSize", "-44")
                .build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<String> exchange = testRestTemplate.exchange(uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), String.class
        );

        JsonContent<Object> jsonContent = json.from(exchange.getBody());

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(jsonContent).isNotNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.type").isEqualToIgnoringCase("about:blank");
        assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualToIgnoringCase("OK");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.status").isEqualTo(200);
        assertThat(jsonContent).extractingJsonPathStringValue("$.detail").isEqualToIgnoringCase("Validation failure");
        assertThat(jsonContent).extractingJsonPathStringValue("$.instance").isEqualToIgnoringCase("/words");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.pageNumber[0].message").isEqualToIgnoringCase("must be greater than or equal to 0");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.pageNumber[0].value").isEqualToIgnoringCase("-51");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.pageSize[0].message").isEqualToIgnoringCase("must be greater than 0");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.pageSize[0].value").isEqualToIgnoringCase("-44");
    }

    @Test
    void findByIdShouldReturnFoundWord() {
        // given
        LocalDateTime start = LocalDateTime.now();
        Word word = WordFactory.create();
        wordRepository.save(word);
        List<Word> words = wordRepository.findAll();
        Long id = words.get(0).getId();

        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words/{wordId}")
                .buildAndExpand(id)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<String> exchange = testRestTemplate.exchange(uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), String.class
        );
        JsonContent<Object> jsonContent = json.from(exchange.getBody());

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(jsonContent).isNotNull();
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(id.intValue());
        assertThat(jsonContent).extractingJsonPathStringValue("$.value").isEqualToIgnoringCase(word.getValue());
        assertThat(jsonContent).extractingJsonPathStringValue("$.language").isEqualTo(word.getLanguage().name());
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .satisfies(new Condition<>(s -> LocalDateTime.parse(s, format).isAfter(start), "Created should be after '%s'", start));
    }

    @Test
    void findByIdShouldReturnNoWordFoundErrorDetail() {
        // given
        Long wordId = 982231L;
        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words/{wordId}")
                .buildAndExpand(wordId)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<String> exchange = testRestTemplate.exchange(uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), String.class
        );
        JsonContent<Object> jsonContent = json.from(exchange.getBody());

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(jsonContent).isNotNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.type").isEqualToIgnoringCase("about:blank");
        assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualToIgnoringCase("OK");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.status").isEqualTo(200);
        assertThat(jsonContent).extractingJsonPathStringValue("$.detail").isEqualToIgnoringCase(String.format("No word with id %d found", wordId));
        assertThat(jsonContent).extractingJsonPathStringValue("$.instance").isEqualToIgnoringCase(String.format("/words/%d", wordId));
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties").isNull();
    }

    @Test
    void findByIdShouldReturnValidationFailureOnNegativeWordIdGiven() {
        // given
        Long wordId = -3129L;
        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words/{wordId}")
                .buildAndExpand(wordId)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<String> exchange = testRestTemplate.exchange(uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), String.class
        );
        JsonContent<Object> jsonContent = json.from(exchange.getBody());

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(jsonContent).isNotNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.type").isEqualToIgnoringCase("about:blank");
        assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualToIgnoringCase("OK");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.status").isEqualTo(200);
        assertThat(jsonContent).extractingJsonPathStringValue("$.detail").isEqualToIgnoringCase("Validation failure");
        assertThat(jsonContent).extractingJsonPathStringValue("$.instance").isEqualToIgnoringCase(String.format("/words/%d", wordId));
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.wordId[0].message").isEqualToIgnoringCase("must be greater than or equal to 0");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.wordId[0].value").isEqualTo(String.valueOf(wordId));
    }

    @Test
    void saveShouldSaveAWord() {
        // given
        LocalDateTime start = LocalDateTime.now();
        WordDTO wordDTO = WordFactory.createWordDTO();
        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<String> exchange = testRestTemplate.exchange(uri,
                HttpMethod.POST,
                new HttpEntity<>(wordDTO, headers), String.class
        );
        JsonContent<Object> jsonContent = json.from(exchange.getBody());

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(jsonContent).isNotNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.value").isEqualToIgnoringCase(wordDTO.getValue());
        assertThat(jsonContent).extractingJsonPathStringValue("$.language").isEqualToIgnoringCase(wordDTO.getLanguage().name());
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .satisfies(new Condition<>(s -> LocalDateTime.parse(s, format).isAfter(start), "Created should be after '%s'", start));
    }

    @Test
    void saveShouldReturnValidationFailureOnNoValueGiven() {
        // given
        WordDTO wordDTO = WordFactory.createWordDTO();
        wordDTO.setValue("");
        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<String> exchange = testRestTemplate.exchange(uri,
                HttpMethod.POST,
                new HttpEntity<>(wordDTO, headers), String.class
        );
        JsonContent<Object> jsonContent = json.from(exchange.getBody());

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(jsonContent).isNotNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.type").isEqualToIgnoringCase("about:blank");
        assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualToIgnoringCase("OK");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.status").isEqualTo(200);
        assertThat(jsonContent).extractingJsonPathStringValue("$.detail").isEqualToIgnoringCase("Invalid request content.");
        assertThat(jsonContent).extractingJsonPathStringValue("$.instance").isEqualToIgnoringCase("/words");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.value[0].message").isEqualToIgnoringCase("must not be blank");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.value[0].value").isEqualTo(wordDTO.getValue());
    }

    @Test
    void saveShouldReturnValidationFailureOnNoLanguageGiven() {
        // given
        WordDTO wordDTO = WordFactory.createWordDTO();
        wordDTO.setLanguage(null);
        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<String> exchange = testRestTemplate.exchange(uri,
                HttpMethod.POST,
                new HttpEntity<>(wordDTO, headers), String.class
        );
        JsonContent<Object> jsonContent = json.from(exchange.getBody());

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(jsonContent).isNotNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.type").isEqualToIgnoringCase("about:blank");
        assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualToIgnoringCase("OK");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.status").isEqualTo(200);
        assertThat(jsonContent).extractingJsonPathStringValue("$.detail").isEqualToIgnoringCase("Invalid request content.");
        assertThat(jsonContent).extractingJsonPathStringValue("$.instance").isEqualToIgnoringCase("/words");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.language[0].message").isEqualToIgnoringCase("must not be null");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.language[0].value").isEqualTo(String.valueOf(wordDTO.getLanguage()));
    }

    @Test
    void saveShouldReturnValidationFailureOnNoValueAndLanguageGiven() {
        // given
        WordDTO wordDTO = WordFactory.createWordDTO();
        wordDTO.setValue(null);
        wordDTO.setLanguage(null);
        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<String> exchange = testRestTemplate.exchange(uri,
                HttpMethod.POST,
                new HttpEntity<>(wordDTO, headers), String.class
        );
        JsonContent<Object> jsonContent = json.from(exchange.getBody());

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(jsonContent).isNotNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.type").isEqualToIgnoringCase("about:blank");
        assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualToIgnoringCase("OK");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.status").isEqualTo(200);
        assertThat(jsonContent).extractingJsonPathStringValue("$.detail").isEqualToIgnoringCase("Invalid request content.");
        assertThat(jsonContent).extractingJsonPathStringValue("$.instance").isEqualToIgnoringCase("/words");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.language[0].message").isEqualToIgnoringCase("must not be null");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.language[0].value").isEqualTo(String.valueOf(wordDTO.getLanguage()));
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.value[0].message").isEqualToIgnoringCase("must not be blank");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.value[0].value").isEqualTo(String.valueOf(wordDTO.getLanguage()));
    }

    @Test
    void saveShouldReturnFailureOnDuplicateWordSavingAttempt() {
        // given
        WordDTO wordDTO = WordFactory.createWordDTO();
        Word word = wordMapper.convertToEntity(wordDTO);

        wordRepository.save(word);

        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words")
                .build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<String> exchange = testRestTemplate.exchange(uri,
                HttpMethod.POST,
                new HttpEntity<>(wordDTO, headers), String.class
        );
        JsonContent<Object> jsonContent = json.from(exchange.getBody());

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(jsonContent).isNotNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.type").isEqualToIgnoringCase("about:blank");
        assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualToIgnoringCase("OK");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.status").isEqualTo(200);
        assertThat(jsonContent).extractingJsonPathStringValue("$.detail").isEqualToIgnoringCase(String.format("Word '%s' already exists!", word.getValue()));
        assertThat(jsonContent).extractingJsonPathStringValue("$.instance").isEqualToIgnoringCase("/words");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties").isNull();
    }

    @Test
    void deleteByIdShouldDeleteWordFromRepository() {
        // given
        Word word = WordFactory.create();
        Word saved = wordRepository.save(word);

        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words/{wordId}")
                .buildAndExpand(saved.getId()).toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<String> exchange = testRestTemplate.exchange(uri,
                HttpMethod.DELETE,
                new HttpEntity<>(headers), String.class
        );

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_LENGTH).get(0)).isEqualTo("0");
        assertThat(exchange.getBody()).isNull();
        assertThat(wordRepository.findById(word.getId())).isEmpty();
    }

    @Test
    void deleteByIdShouldReturnValidationFailureOnNegativeIdGiven() {
        // given
        Long id = -321L;
        URI uri = UriComponentsBuilder
                .fromHttpUrl(SERVER_URL)
                .path("/words/{wordId}")
                .buildAndExpand(id.toString()).toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // when
        ResponseEntity<String> exchange = testRestTemplate.exchange(uri,
                HttpMethod.DELETE,
                new HttpEntity<>(headers), String.class
        );
        JsonContent<Object> jsonContent = json.from(exchange.getBody());

        // then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(jsonContent).isNotNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.type").isEqualToIgnoringCase("about:blank");
        assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualToIgnoringCase("OK");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.status").isEqualTo(200);
        assertThat(jsonContent).extractingJsonPathStringValue("$.detail").isEqualToIgnoringCase("Validation failure");
        assertThat(jsonContent).extractingJsonPathStringValue("$.instance").isEqualToIgnoringCase(String.format("/words/%d", id));
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.wordId[0].message").isEqualToIgnoringCase("must be greater than or equal to 0");
        assertThat(jsonContent).extractingJsonPathStringValue("$.properties.wordId[0].value").isEqualTo(id.toString());
    }

}
