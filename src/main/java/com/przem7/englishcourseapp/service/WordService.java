package com.przem7.englishcourseapp.service;

import com.przem7.englishcourseapp.exception.WordAlreadyExistsException;
import com.przem7.englishcourseapp.exception.WordNotFoundException;
import com.przem7.englishcourseapp.model.orm.Word;
import com.przem7.englishcourseapp.repository.WordRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class WordService {

    @Autowired
    private WordRepository wordRepository;

    @Transactional
    public List<Word> getWords(Integer pageNumber,
                               Integer pageSize,
                               List<String> sortByColumns,
                               String containing,
                               LocalDateTime dateFrom,
                               LocalDateTime dateTo) {
        List<Sort.Order> orders = new ArrayList<>();
        for (String columnName : sortByColumns) {
            orders.add(Sort.Order.by(columnName));
        }
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(orders));

        if (dateFrom == null && dateTo == null) {
            return StreamSupport
                    .stream(wordRepository
                            .findAllByValueContaining(containing, pageRequest)
                            .spliterator(), false)
                    .collect(Collectors.toList());
        }

        if (dateFrom != null && dateTo == null) {
            return StreamSupport
                    .stream(wordRepository
                            .findAllByValueContainingAndCreatedAfter(containing, dateFrom, pageRequest)
                            .spliterator(), false)
                    .collect(Collectors.toList());
        }

        if (dateFrom == null) {
            return StreamSupport
                    .stream(wordRepository
                            .findAllByValueContainingAndCreatedBefore(containing, dateTo, pageRequest)
                            .spliterator(), false)
                    .collect(Collectors.toList());
        }

        return StreamSupport
                .stream(wordRepository
                        .findAllByValueContainingAndCreatedBetween(containing,
                                dateFrom,
                                dateTo,
                                pageRequest)
                        .spliterator(), false)
                .collect(Collectors.toList());
    }

    @Transactional
    public Word findById(Long id) throws WordNotFoundException {
        Optional<Word> optionalWord = wordRepository.findById(id);
        if (optionalWord.isEmpty()) {
            throw new WordNotFoundException(id);
        }
        return optionalWord.get();
    }

    @Transactional
    public Word save(Word word) throws WordAlreadyExistsException {
        if (wordRepository.findTopByValue(word.getValue()) != null) {
            throw new WordAlreadyExistsException(word);
        }

        Word saved = wordRepository.save(word);
        log.info("word " + saved + " saved successfully!");

        return saved;
    }

    public void deleteById(Long wordId) {
        wordRepository.deleteById(wordId);
    }
}
