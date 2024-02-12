package com.przem7.englishcourseapp.mapper;

import com.przem7.englishcourseapp.model.dto.WordStatisticsDTO;
import com.przem7.englishcourseapp.model.orm.WordStatistics;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class WordStatisticsMapper {

    @Autowired
    @Qualifier("wordStatisticsModelMapper")
    private ModelMapper modelMapper;

    public WordStatisticsDTO convertToDto(WordStatistics wordStatistics) {
        return modelMapper.map(wordStatistics, WordStatisticsDTO.class);
    }

    public WordStatistics convertToEntity(WordStatisticsDTO wordStatisticsDto) {
        return modelMapper.map(wordStatisticsDto, WordStatistics.class);
    }
}
