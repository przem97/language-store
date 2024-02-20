package com.przem7.englishcourseapp.repository;

import com.przem7.englishcourseapp.model.orm.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long> {
    public Word findTopByValue(String value);
}
