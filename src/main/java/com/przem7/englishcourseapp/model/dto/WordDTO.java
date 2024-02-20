package com.przem7.englishcourseapp.model.dto;

import com.przem7.englishcourseapp.model.orm.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordDTO {

    protected Long id;
    protected String value;
    protected Language language;
    protected LocalDateTime created;
}
