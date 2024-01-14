package com.przem7.englishcourseapp.model.orm;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class WordStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private Word word;

    @OneToMany
    private List<FailureMatch> failureMatches;

    @OneToMany
    private List<SuccessMatch> successMatches;
}
