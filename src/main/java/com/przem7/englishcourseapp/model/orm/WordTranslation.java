package com.przem7.englishcourseapp.model.orm;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class WordTranslation {

    @Id
    private Long id;

    @OneToOne
    private Word word;

    @OneToMany
    private List<Word> translations;
}
