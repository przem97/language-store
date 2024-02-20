package com.przem7.englishcourseapp.repository;

import com.przem7.englishcourseapp.model.orm.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface WordRepository extends JpaRepository<Word, Long> {
    Word findTopByValue(String value);
    Page<Word> findAllByValueContaining(String in, Pageable pageable);
    Page<Word> findAllByValueContainingAndCreatedAfter(String in, LocalDateTime dateFrom, Pageable pageable);
    Page<Word> findAllByValueContainingAndCreatedBefore(String in, LocalDateTime dateTo, Pageable pageable);
    Page<Word> findAllByValueContainingAndCreatedBetween(String in, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);
}
