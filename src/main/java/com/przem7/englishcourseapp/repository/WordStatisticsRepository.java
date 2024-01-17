package com.przem7.englishcourseapp.repository;

import com.przem7.englishcourseapp.model.orm.Word;
import com.przem7.englishcourseapp.model.orm.WordStatistics;
import org.springframework.data.repository.CrudRepository;

public interface WordStatisticsRepository extends CrudRepository<WordStatistics, Long> {

    WordStatistics findTopByWord(Word word);
}
