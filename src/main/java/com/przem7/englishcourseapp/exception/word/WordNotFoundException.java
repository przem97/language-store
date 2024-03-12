package com.przem7.englishcourseapp.exception.word;

import com.przem7.englishcourseapp.exception.EnglishCourseStoreException;

public class WordNotFoundException extends EnglishCourseStoreException {

    public WordNotFoundException(Long id) {
        super("No word with id " + id + " found");
    }
}
