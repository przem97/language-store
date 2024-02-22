package com.przem7.englishcourseapp.controller;

import com.przem7.englishcourseapp.exception.word.WordAlreadyExistsException;
import com.przem7.englishcourseapp.exception.word.WordNotFoundException;
import com.przem7.englishcourseapp.mapper.WordMapper;
import com.przem7.englishcourseapp.model.dto.MatchDTO;
import com.przem7.englishcourseapp.model.dto.WordDTO;
import com.przem7.englishcourseapp.model.dto.WordDTOWithTranslations;
import com.przem7.englishcourseapp.model.dto.WordStatisticsDTO;
import com.przem7.englishcourseapp.model.orm.Word;

import com.przem7.englishcourseapp.service.MatchService;
import com.przem7.englishcourseapp.service.WordService;
import com.przem7.englishcourseapp.service.WordStatisticsService;
import com.przem7.englishcourseapp.validate.WordValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    private WordValidator wordValidator;

    @Autowired
    private WordMapper wordMapper;

    @GetMapping("/words")
    public ResponseEntity<List<WordDTO>> getWords(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "100") Integer pageSize,
            @RequestParam(value = "sortByColumns", required = false, defaultValue = "id") List<String> sortByColumns,
            @RequestParam(value = "containing", required = false, defaultValue = "") String containing,
            @RequestParam(value = "dateFrom", required = false) LocalDateTime dateFrom,
            @RequestParam(value = "dateTo", required = false) LocalDateTime dateTo) {
        return ResponseEntity.ok(wordService
                .getWords(pageNumber, pageSize, sortByColumns, containing, dateFrom, dateTo)
                .stream()
                .map(wordMapper::convertToDto)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/words/{wordId}")
    public ResponseEntity<WordDTO> findById(@PathVariable("wordId") Long wordId) throws WordNotFoundException {
        Word word = wordService.findById(wordId);
        return ResponseEntity.ok(wordMapper.convertToDto(word));
    }

    @PostMapping("/words")
    public ResponseEntity<WordDTO> save(@RequestBody WordDTOWithTranslations wordDto) throws WordAlreadyExistsException {
        Word word = wordMapper.convertToEntity(wordDto);
        return ResponseEntity.ok(wordMapper.convertToDto(wordService.save(word)));
    }

    @DeleteMapping("/words/{wordId}")
    public ResponseEntity<Void> deleteById(@PathVariable("wordId") Long wordId) {
        wordService.deleteById(wordId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/words/{wordId}/statistics")
    public ResponseEntity<WordStatisticsDTO> getStatisticsByWordId(@PathVariable("wordId") Long wordId) {
        return ResponseEntity.ok(wordStatisticsService.getWordStatisticsByWordId(wordId));
    }

    @PostMapping("/words/{wordId}/matches")
    public ResponseEntity<Void> save(@PathVariable("wordId") Long wordId,
                                     @RequestBody MatchDTO matchDto) throws WordNotFoundException {
        wordValidator.validateIfExists(wordId);
        matchService.save(wordId, matchDto);

        return ResponseEntity.ok().build();
    }
}
