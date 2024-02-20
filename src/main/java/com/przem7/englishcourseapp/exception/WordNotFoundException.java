package com.przem7.englishcourseapp.exception;

import com.przem7.englishcourseapp.model.orm.Word;

public class WordNotFoundException extends Exception {

    public WordNotFoundException(Long id) {
        super("No word with id " + id + " found");
    }
}
