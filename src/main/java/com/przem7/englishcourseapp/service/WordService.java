package com.przem7.englishcourseapp.service;

import com.przem7.englishcourseapp.model.orm.Word;
import com.przem7.englishcourseapp.repository.WordRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Word> getWords() {
        return StreamSupport
                .stream(wordRepository
                        .findAll()
                        .spliterator(), false)
                .collect(Collectors.toList());
    }

    public Optional<Word> findById(Long id) {
        return wordRepository.findById(id);
    }

    @Transactional
    public Word save(Word word) {
        Set<Word> translations = word.getTranslations();
        word.setTranslations(null);

        word = wordRepository.save(word);

        for (Word translation : translations) {
            Word dbTranslation = wordRepository.findTopByValue(translation.getValue());

            if (dbTranslation != null) {
                dbTranslation.getTranslations().add(word);
                Word savedTranslation = wordRepository.save(translation);
                translation.setId(savedTranslation.getId());
            }
        }

        word.setTranslations(translations);
        Word saved = wordRepository.save(word);

        return saved;
    }

    public void deleteById(Long wordId) {
        wordRepository.deleteById(wordId);
    }
}
