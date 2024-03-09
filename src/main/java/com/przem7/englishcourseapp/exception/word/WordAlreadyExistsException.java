package com.przem7.englishcourseapp.exception.word;

import com.przem7.englishcourseapp.exception.EnglishCourseStoreException;
import com.przem7.englishcourseapp.model.orm.Word;

public class WordAlreadyExistsException extends EnglishCourseStoreException {

    public WordAlreadyExistsException(Word word) {
        super("Word '" + word.getValue() + "' already exists!");
    }
}
