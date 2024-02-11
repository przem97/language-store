package com.przem7.englishcourseapp.service;

import com.przem7.englishcourseapp.model.dto.WordStatisticsDTO;
import com.przem7.englishcourseapp.model.orm.Word;
import com.przem7.englishcourseapp.model.orm.WordStatistics;
import com.przem7.englishcourseapp.repository.WordRepository;
import com.przem7.englishcourseapp.repository.WordStatisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class WordStatisticsService {

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private WordStatisticsRepository wordStatisticsRepository;

    @Autowired
    private ModelMapper mapper;

    @Transactional
    public WordStatisticsDTO getWordStatisticsByWordId(Long wordId) {
        Optional<Word> word = wordRepository.findById(wordId);

        if (word.isPresent()) {
            WordStatistics wordStatistics = wordStatisticsRepository.findTopByWord(word.get());
            return mapper.map(wordStatistics, WordStatisticsDTO.class);
        }

        return null;
    }
}
