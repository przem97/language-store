package com.przem7.englishcourseapp.model.dto;

import com.przem7.englishcourseapp.model.orm.Language;
import com.przem7.englishcourseapp.validation.group.CreateWord;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordDTO {

    @PositiveOrZero
    protected Long id;

    @NotBlank(groups = CreateWord.class)
    protected String value;

    @NotNull(groups = CreateWord.class)
    protected Language language;

    protected LocalDateTime created;
}
