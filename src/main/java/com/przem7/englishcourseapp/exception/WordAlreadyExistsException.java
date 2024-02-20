package com.przem7.englishcourseapp.exception;

import com.przem7.englishcourseapp.model.orm.Word;

public class WordAlreadyExistsException extends Exception {

    public WordAlreadyExistsException(Word word) {
        super("Word '" + word.getValue() + "' already exists!");
    }
}
