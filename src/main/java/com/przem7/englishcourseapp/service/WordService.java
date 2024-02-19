package com.przem7.englishcourseapp.service;

import com.przem7.englishcourseapp.exception.WordNotFoundException;
import com.przem7.englishcourseapp.model.orm.Word;
import com.przem7.englishcourseapp.repository.WordRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class WordService {

    @Autowired
    private WordRepository wordRepository;

    @Transactional
    public List<Word> getWords() {
        return StreamSupport
                .stream(wordRepository
                        .findAll()
                        .spliterator(), false)
                .peek(x -> Hibernate.initialize(x.getTranslations()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Word findById(Long id) throws WordNotFoundException {
        Optional<Word> optionalWord = wordRepository.findById(id);
        if (optionalWord.isEmpty()) {
            throw new WordNotFoundException("No word with id " + id + " found");
        }
        optionalWord.ifPresent(word -> Hibernate.initialize(word.getTranslations()));
        return optionalWord.get();
    }

    @Transactional
    public Word save(Word word) {
        Set<Word> translations = word.getTranslations();
        Set<Word> savedTranslations = new HashSet<>();
        word.setTranslations(null);

        word = wordRepository.save(word);
        log.info("word: " + word + " saved successfully!");

        for (Word translation : translations) {
            Word dbTranslation = wordRepository.findTopByValue(translation.getValue());

            if (dbTranslation != null) {
                log.info("updating dbTranslation " + dbTranslation);
                dbTranslation.getTranslations().add(word);
                Word savedTranslation = wordRepository.save(dbTranslation);
                savedTranslations.add(savedTranslation);
            } else {
                log.info("dbTranslation of " + translation.getValue() + " not found!");
            }
        }

        log.info("setting translations for word: " + word);
        word.setTranslations(savedTranslations);
        Word saved = wordRepository.save(word);

        log.info("word: " + word + " updated successfully with translations: " + word.getTranslations());

        return saved;
    }

    public void deleteById(Long wordId) {
        wordRepository.deleteById(wordId);
    }
}
