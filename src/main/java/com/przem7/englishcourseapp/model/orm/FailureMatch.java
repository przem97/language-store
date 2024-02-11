package com.przem7.englishcourseapp.model.orm;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class FailureMatch extends Match {

    @ManyToOne
    @JoinColumn(name = "word_statistics_id")
    private WordStatistics wordStatistics;

    private String errorValue;

    private Integer levenshteinDistance;
}
