package com.przem7.englishcourseapp.repository;

import com.przem7.englishcourseapp.model.Word;
import org.springframework.data.repository.CrudRepository;

public interface WordRepository extends CrudRepository<Word, Long> {
}