package com.przem7.englishcourseapp.mapper;

import com.przem7.englishcourseapp.model.dto.WordDto;
import com.przem7.englishcourseapp.model.orm.Word;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WordMapper {

    @Autowired
    private ModelMapper modelMapper;

    public WordDto convertToDto(Word word) {
        return modelMapper.map(word, WordDto.class);
    }

    public Word convertToEntity(WordDto wordDto) {
        return modelMapper.map(wordDto, Word.class);
    }
}
