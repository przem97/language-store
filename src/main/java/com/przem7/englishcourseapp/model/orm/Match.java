package com.przem7.englishcourseapp.model.orm;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    protected Long millisWaited;
    protected LocalDateTime stamp;
}
