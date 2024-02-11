package com.przem7.englishcourseapp.controller;

import com.przem7.englishcourseapp.model.dto.WordStatisticsDTO;
import com.przem7.englishcourseapp.service.WordStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class WordStatisticsController {

    @Autowired
    private WordStatisticsService wordStatisticsService;

    @GetMapping("/statistics/")
    public ResponseEntity<WordStatisticsDTO> getStatistics() {
        // TODO : implement

        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistics/{statisticId}")
    public ResponseEntity<WordStatisticsDTO> findStatisticsById(@PathVariable("statisticId") Long statisticId) {
        // TODO : implement

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/statistics/{statisticId}")
    public ResponseEntity<Void> deleteByStatisticsId(@PathVariable("statisticId") Long statisticId) {
        // TODO : implement

        return ResponseEntity.ok().build();
    }
}
