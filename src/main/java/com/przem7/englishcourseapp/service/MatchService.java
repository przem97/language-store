package com.przem7.englishcourseapp.service;

import com.przem7.englishcourseapp.model.dto.MatchDTO;
import com.przem7.englishcourseapp.model.orm.FailureMatch;
import com.przem7.englishcourseapp.model.orm.SuccessMatch;
import com.przem7.englishcourseapp.model.orm.Word;
import com.przem7.englishcourseapp.model.orm.WordStatistics;
import com.przem7.englishcourseapp.repository.WordRepository;
import com.przem7.englishcourseapp.repository.WordStatisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class MatchService {

    @Autowired
    private WordStatisticsRepository wordStatisticsRepository;

    @Autowired
    private WordRepository wordRepository;

    public void save(Long wordId, MatchDTO matchDto) {
        log.info("addMatch() - START");

        Optional<Word> baseWord = wordRepository.findById(wordId);
        WordStatistics wordStatistics = wordStatisticsRepository.findTopByWord(baseWord.get());

        if (wordStatistics == null) {
            log.info("wordStatistics for word: " + baseWord.get().getValue() + " do not exist, creating new wordStatistics");
            wordStatistics = new WordStatistics();
            wordStatistics.setWord(baseWord.get());
        }

        if (!matchDto.getAnswer().equals(baseWord.get().getValue())) {
            log.info("saving success match");
            SuccessMatch successMatch = new SuccessMatch(wordStatistics);

            wordStatistics.getSuccessMatches().add(successMatch);
            wordStatisticsRepository.save(wordStatistics);
        } else {
            log.info("saving failure match");
            FailureMatch failureMatch = new FailureMatch(wordStatistics, matchDto.getAnswer());

            wordStatistics.getFailureMatches().add(failureMatch);
            wordStatisticsRepository.save(wordStatistics);
        }
    }
}
