package com.przem7.englishcourseapp.mapper;

import com.przem7.englishcourseapp.model.dto.WordDTO;
import com.przem7.englishcourseapp.model.dto.WordDTOWithTranslations;
import com.przem7.englishcourseapp.model.orm.Word;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class WordMapper {

    private final ModelMapper modelMapper;

    public WordMapper(@Qualifier("wordModelMapper") ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public WordDTOWithTranslations convertToDtoWithTranslations(Word word) {
        return modelMapper.map(word, WordDTOWithTranslations.class);
    }

    public WordDTO convertToDto(Word word) {
        return modelMapper.map(word, WordDTO.class);
    }

    public Word convertToEntity(WordDTOWithTranslations wordDto) {
        return modelMapper.map(wordDto, Word.class);
    }
}
