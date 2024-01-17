package com.przem7.englishcourseapp.model.orm;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FailureMatch extends Match {

    @ManyToOne
    private WordStatistics wordStatistics;

    private String errorValue;

    private Integer levenshteinDistance;
}
