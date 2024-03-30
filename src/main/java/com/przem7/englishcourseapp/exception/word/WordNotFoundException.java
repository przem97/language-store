package com.przem7.englishcourseapp.exception.word;

import com.przem7.englishcourseapp.exception.EnglishCourseStoreException;
import lombok.Getter;

@Getter
public class WordNotFoundException extends EnglishCourseStoreException {

    private final Long argument;

    public WordNotFoundException(Long id) {
        super("No word with id " + id + " found");
        this.argument = id;
    }
}
