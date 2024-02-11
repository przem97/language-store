package com.przem7.englishcourseapp.controller;

import com.przem7.englishcourseapp.exception.WordNotFoundException;
import com.przem7.englishcourseapp.mapper.WordMapper;
import com.przem7.englishcourseapp.model.dto.MatchDTO;
import com.przem7.englishcourseapp.model.dto.WordDTO;
import com.przem7.englishcourseapp.model.dto.WordStatisticsDTO;
import com.przem7.englishcourseapp.model.orm.Word;

import com.przem7.englishcourseapp.service.MatchService;
import com.przem7.englishcourseapp.service.WordService;
import com.przem7.englishcourseapp.service.WordStatisticsService;
import com.przem7.englishcourseapp.validate.WordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
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
    public ResponseEntity<List<Word>> getWords() {
        return ResponseEntity.ok(wordService.getWords());
    }

    @GetMapping("/words/{wordId}")
    public ResponseEntity<Word> findById(@PathVariable("wordId") Long wordId) {
        Optional<Word> word = wordService.findById(wordId);
        return word.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/words")
    public ResponseEntity<Word> save(@RequestBody WordDTO wordDto) {
        return ResponseEntity.ok(wordService.save(wordMapper.convertToEntity(wordDto)));
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
