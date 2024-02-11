package com.przem7.englishcourseapp.mapper;

import com.przem7.englishcourseapp.model.dto.WordDTO;
import com.przem7.englishcourseapp.model.orm.Word;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WordMapper {

    @Autowired
    private ModelMapper modelMapper;

    public WordDTO convertToDto(Word word) {
        return modelMapper.map(word, WordDTO.class);
    }

    public Word convertToEntity(WordDTO wordDto) {
        return modelMapper.map(wordDto, Word.class);
    }
}
