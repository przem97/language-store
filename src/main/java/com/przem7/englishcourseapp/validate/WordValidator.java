package com.przem7.englishcourseapp.validate;

import com.przem7.englishcourseapp.exception.WordNotFoundException;
import com.przem7.englishcourseapp.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WordValidator {

    @Autowired
    private WordService wordService;

    public void validateIfExists(Long wordId) throws WordNotFoundException {
        if (wordService.findById(wordId).isEmpty()) {
            throw new WordNotFoundException(String.format("Word with id: %d not found", wordId));
        }
    }
}
