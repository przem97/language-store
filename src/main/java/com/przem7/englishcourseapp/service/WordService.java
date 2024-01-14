package com.przem7.englishcourseapp.service;

import com.przem7.englishcourseapp.model.Word;
import com.przem7.englishcourseapp.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class WordService {

    @Autowired
    private WordRepository wordRepository;

    public List<Word> getAllWords() {
        return StreamSupport
                .stream(wordRepository
                        .findAll()
                        .spliterator(), false)
                .collect(Collectors.toList());
    }

    public Word saveWord(Word word) {
        Word saved = wordRepository.save(word);
        return saved;
    }
}
