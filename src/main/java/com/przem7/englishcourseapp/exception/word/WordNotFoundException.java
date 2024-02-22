package com.przem7.englishcourseapp.exception.word;

public class WordNotFoundException extends Exception {

    public WordNotFoundException(Long id) {
        super("No word with id " + id + " found");
    }
}
