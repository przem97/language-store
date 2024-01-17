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

    @OneToMany(cascade = CascadeType.ALL)
    private List<FailureMatch> failureMatches;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SuccessMatch> successMatches;
}
