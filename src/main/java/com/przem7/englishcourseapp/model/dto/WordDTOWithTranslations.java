package com.przem7.englishcourseapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordDTOWithTranslations extends WordDTO {

    protected List<WordDTO> translations;
}
