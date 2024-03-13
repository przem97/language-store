package com.przem7.englishcourseapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.przem7.englishcourseapp.controller.factory.WordFactory;
import com.przem7.englishcourseapp.controller.util.LocalDateTimeUtil;
import com.przem7.englishcourseapp.exception.word.WordNotFoundException;
import com.przem7.englishcourseapp.mapper.WordMapper;
import com.przem7.englishcourseapp.model.dto.WordDTO;
import com.przem7.englishcourseapp.model.orm.Word;
import com.przem7.englishcourseapp.service.MatchService;
import com.przem7.englishcourseapp.service.WordService;
import com.przem7.englishcourseapp.service.WordStatisticsService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WordController.class)
public class WordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WordService wordService;

    @MockBean
    private WordStatisticsService wordStatisticsService;

    @MockBean
    private MatchService matchService;

    @Autowired
    private WordMapper wordMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class AdditionalConfiguration {
        @Bean
        public WordMapper wordModelMapper() {
            return new WordMapper(new ModelMapper());
        }
    }

    @Test
    void getWordsShouldReturnEmptyArray() throws Exception {
        // given
        given(wordService.getWords(anyInt(),
                anyInt(),
                anyList(),
                anyString(),
                any(),
                any())).willReturn(Collections.emptyList());

        // when
        ResultActions getWords = this.mockMvc.perform(get("/words"));

        // then
        getWords.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("[]"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getWordsShouldReturnArrayOfOneWord() throws Exception {
        // given
        List<Word> words = new ArrayList<>();
        Word word = WordFactory.create();
        words.add(word);

        given(wordService.getWords(anyInt(),
                anyInt(),
                anyList(),
                anyString(),
                any(),
                any())).willReturn(words);

        // when
        ResultActions getWords = this.mockMvc.perform(get("/words"));

        // then
        getWords.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].id").value(word.getId()))
                .andExpect(jsonPath("$[0].value").value(word.getValue()))
                .andExpect(jsonPath("$[0].language").value(word.getLanguage().name()))
                .andExpect(jsonPath("$[0].created").value(word.getCreated().toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void findByIdShouldReturnFoundWord() throws Exception {
        // given
        Word word = WordFactory.create();

        given(wordService.findById(anyLong())).willReturn(word);

        // when
        ResultActions findWordById = this.mockMvc.perform(get("/words/{id}", word.getId())
                .accept(MediaType.APPLICATION_JSON));

        // then
        findWordById.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(word.getId()))
                .andExpect(jsonPath("$.value").value(word.getValue()))
                .andExpect(jsonPath("$.language").value(word.getLanguage().name()))
                .andExpect(jsonPath("$.created").value(word.getCreated().toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void findByIdShouldHandleWordNotFoundException() throws Exception {
        // given
        Long id = 432L;
        Throwable throwable = new WordNotFoundException(id);
        given(wordService.findById(anyLong())).willThrow(throwable);

        // when
        ResultActions findWordById = this.mockMvc.perform(get("/words/{id}", id));

        // then
        findWordById.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.TEXT_HTML + ";charset=" + StandardCharsets.UTF_8));
    }

    @Test
    void saveShouldSaveAWord() throws Exception {
        // given
        Word word = WordFactory.create();
        WordDTO wordDTO = wordMapper.convertToDto(word);

        given(wordService.save(any(Word.class))).willReturn(word);
        String str = objectMapper.writeValueAsString(wordDTO);

        // when
        ResultActions saveWord = this.mockMvc.perform(MockMvcRequestBuilders.post("/words")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(str));

        // then
        saveWord.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(wordDTO.getId()))
                .andExpect(jsonPath("$.value").value(wordDTO.getValue()))
                .andExpect(jsonPath("$.language").value(wordDTO.getLanguage().toString()))
                .andExpect(LocalDateTimeUtil.isOfDesiredFormat("$.created", DateTimeFormatter.ISO_DATE_TIME))
                .andExpect(LocalDateTimeUtil.isEqual("$.created", wordDTO.getCreated(), DateTimeFormatter.ISO_DATE_TIME));
    }
}
