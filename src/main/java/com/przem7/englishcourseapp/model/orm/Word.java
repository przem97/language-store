package com.przem7.englishcourseapp.model.orm;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String word;

    @Column
    private Language language;

    public Word(String word) {
        this.word = word;
    }

}
