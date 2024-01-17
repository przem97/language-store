package com.przem7.englishcourseapp.model.dto;

import com.przem7.englishcourseapp.model.orm.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordDTO {

    private Long id;
    private String word;
    private Language language;
}
