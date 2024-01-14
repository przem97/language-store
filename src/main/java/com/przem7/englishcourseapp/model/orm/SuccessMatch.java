package com.przem7.englishcourseapp.model.orm;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class SuccessMatch extends Match {

    @Id
    private Long id;

    @OneToOne
    private Word successMatchWord;

}
