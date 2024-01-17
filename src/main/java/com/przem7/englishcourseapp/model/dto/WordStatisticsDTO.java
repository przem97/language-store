package com.przem7.englishcourseapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordStatisticsDTO {

    private String word;
    private Long failureCount;
    private Long successCount;
    private List<String> failureValues;
}
