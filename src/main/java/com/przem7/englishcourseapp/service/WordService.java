package com.przem7.englishcourseapp.service;

import com.przem7.englishcourseapp.model.orm.Word;
import com.przem7.englishcourseapp.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
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

    public Word save(Word word) {
        return wordRepository.save(word);
    }

    public void deleteById(Long wordId) {
        wordRepository.deleteById(wordId);
    }
}
