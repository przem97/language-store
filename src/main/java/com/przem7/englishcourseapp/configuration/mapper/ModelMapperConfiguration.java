package com.przem7.englishcourseapp.configuration.mapper;

import com.przem7.englishcourseapp.configuration.mapper.converter.FailureMatchesToLongConverter;
import com.przem7.englishcourseapp.configuration.mapper.converter.FailureMatchesToStringListConverter;
import com.przem7.englishcourseapp.model.dto.WordStatisticsDTO;
import com.przem7.englishcourseapp.model.orm.WordStatistics;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        setupTypeMapForFailureMatch(modelMapper);

        return modelMapper;
    }

    private void setupTypeMapForFailureMatch(ModelMapper modelMapper) {
        TypeMap<WordStatistics, WordStatisticsDTO> typeMap = modelMapper.createTypeMap(WordStatistics.class, WordStatisticsDTO.class);
        typeMap.addMappings(
                mapping -> mapping.using(new FailureMatchesToStringListConverter())
                        .map(WordStatistics::getFailureMatches, WordStatisticsDTO::setFailureValues)
        );
        typeMap.addMappings(
                mapping -> mapping.using(new FailureMatchesToLongConverter())
                        .map(WordStatistics::getFailureMatches, WordStatisticsDTO::setFailureCount)
        );
    }
}

