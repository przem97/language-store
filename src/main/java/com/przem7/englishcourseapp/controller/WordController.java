package com.przem7.englishcourseapp.controller;

import com.przem7.englishcourseapp.exception.InvalidPayloadException;
import com.przem7.englishcourseapp.exception.word.WordAlreadyExistsException;
import com.przem7.englishcourseapp.exception.word.WordNotFoundException;
import com.przem7.englishcourseapp.mapper.WordMapper;
import com.przem7.englishcourseapp.model.dto.MatchDTO;
import com.przem7.englishcourseapp.model.dto.WordDTO;
import com.przem7.englishcourseapp.model.dto.WordStatisticsDTO;
import com.przem7.englishcourseapp.model.orm.Word;

import com.przem7.englishcourseapp.service.MatchService;
import com.przem7.englishcourseapp.service.WordService;
import com.przem7.englishcourseapp.service.WordStatisticsService;
import com.przem7.englishcourseapp.validation.group.CreateWord;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class WordController {

    @Autowired
    private WordService wordService;

    @Autowired
    private WordStatisticsService wordStatisticsService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private WordMapper wordMapper;

    @GetMapping(
            value = "/words",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Valid
    public ResponseEntity<List<WordDTO>> getWords(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") @PositiveOrZero Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "100") @Positive Integer pageSize,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") @NotEmpty List<String> sortBy,
            @RequestParam(value = "containing", required = false, defaultValue = "") @NotBlank String containing,
            @RequestParam(value = "dateFrom", required = false) @NotNull LocalDateTime dateFrom,
            @RequestParam(value = "dateTo", required = false) @NotNull LocalDateTime dateTo) {
        return ResponseEntity.ok(wordService
                .getWords(pageNumber, pageSize, sortBy, containing, dateFrom, dateTo)
                .stream()
                .map(wordMapper::convertToDto)
                .collect(Collectors.toList())
        );
    }

    @GetMapping(
            value = "/words/{wordId}",
            produces = { MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<WordDTO> findById(@PathVariable("wordId") Long wordId) throws WordNotFoundException {
        Word word = wordService.findById(wordId);
        return ResponseEntity.ok(wordMapper.convertToDto(word));
    }

    @PostMapping(
            value = "/words",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = { MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    @Validated(CreateWord.class)
    public ResponseEntity<WordDTO> save(@RequestBody WordDTO wordDto,
                                        BindingResult result) throws WordAlreadyExistsException,
            InvalidPayloadException {
        if (result.hasErrors()) {
            log.debug("word validation has failed! word=" + wordDto);
            String[] violationMessages = result.getFieldErrors()
                    .stream()
                    .map(x -> x.getField() + " " + x.getDefaultMessage() + " (actual value=" + x.getRejectedValue() + ")")
                    .toArray(String[]::new);

            String message = Arrays.toString(violationMessages);
            throw new InvalidPayloadException(message);
        }

        log.debug("word to save=" + wordDto);
        Word word = wordMapper.convertToEntity(wordDto);

        return ResponseEntity.ok(wordMapper.convertToDto(wordService.save(word)));
    }

    @DeleteMapping("/words/{wordId}")
    @Valid
    public ResponseEntity<Void> deleteById(@PathVariable("wordId") @PositiveOrZero Long wordId) {
        wordService.deleteById(wordId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/words/{wordId}/statistics")
    @Valid
    public ResponseEntity<WordStatisticsDTO> getStatisticsByWordId(@PathVariable("wordId") @PositiveOrZero Long wordId) {
        return ResponseEntity.ok(wordStatisticsService.getWordStatisticsByWordId(wordId));
    }

    @PostMapping("/words/{wordId}/matches")
    @Valid
    public ResponseEntity<Void> save(@PathVariable("wordId") @PositiveOrZero Long wordId,
                                     @RequestBody MatchDTO matchDto) throws WordNotFoundException {
        matchService.save(wordId, matchDto);

        return ResponseEntity.ok().build();
    }
}
