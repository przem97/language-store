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

    @Column(unique = true, nullable = false, updatable = false)
    private String word;

    @Column(nullable = false, updatable = false)
    private Language language;

    public Word(String word) {
        this.word = word;
    }

}
