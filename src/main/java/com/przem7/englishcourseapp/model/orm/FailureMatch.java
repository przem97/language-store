package com.przem7.englishcourseapp.model.orm;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class FailureMatch extends Match {

    @Id
    private Long id;

    private String errorValue;

    private Integer levenshteinDistance;
}
