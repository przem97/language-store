package com.przem7.englishcourseapp.controller.factory;

import com.przem7.englishcourseapp.model.orm.Language;
import com.przem7.englishcourseapp.model.orm.Word;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class WordFactory {

    public static Word create() {
        Faker faker = new Faker();

        Word word = new Word();
        word.setId(faker.number().numberBetween(0, 100000));
        word.setCreated(faker.date().birthday().toLocalDateTime());
        word.setValue(faker.company().buzzword());
        word.setLanguage(Language.ENGLISH);

        return word;
    }

    public static List<Word> createWords(int size) {
        return IntStream.range(0, size).mapToObj(x -> WordFactory.create()).collect(Collectors.toList());
    }
}
