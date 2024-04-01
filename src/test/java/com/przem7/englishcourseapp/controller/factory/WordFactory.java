package com.przem7.englishcourseapp.controller.factory;

import com.przem7.englishcourseapp.model.dto.WordDTO;
import com.przem7.englishcourseapp.model.orm.Language;
import com.przem7.englishcourseapp.model.orm.Word;
import net.datafaker.Faker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static WordDTO createWordDTO() {
        Faker faker = new Faker();

        WordDTO word = new WordDTO();
        word.setId((long) faker.number().numberBetween(0, 100000));
        word.setCreated(faker.date().birthday().toLocalDateTime());
        word.setValue(faker.company().buzzword());
        word.setLanguage(Language.ENGLISH);

        return word;
    }

    public static List<Word> createWords(int size) {
        Set<String> distinctWords = new HashSet<>();
        List<Word> result = new ArrayList<>();

        while (distinctWords.size() < size) {
            Word word = WordFactory.create();

            if (!distinctWords.contains(word.getValue())) {
                word.setId(distinctWords.size() + 1);
                distinctWords.add(word.getValue());
                result.add(word);
            }
        }

        return result;
    }
}
