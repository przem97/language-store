package com.przem7.englishcourseapp.controller;

import com.przem7.englishcourseapp.model.Word;

import com.przem7.englishcourseapp.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WordController {

    @Autowired
    private WordService wordService;

    @GetMapping("/words")
    public ResponseEntity<List<Word>> getWords() {
        return ResponseEntity.ok(wordService.getAllWords());
    }

    @PostMapping("/words")
    public ResponseEntity<Word> saveWord(@RequestBody Word word) {
        return ResponseEntity.ok(wordService.saveWord(word));
    }
}
