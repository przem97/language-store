package com.przem7.englishcourseapp.repository;

import com.przem7.englishcourseapp.model.orm.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long> {
    public Word findTopByValue(String value);
    public Page<Word> findAllByValueContaining(String in, Pageable pageable);
}
