package com.przem7.englishcourseapp.model.orm;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String value;

    @Column(nullable = false, updatable = false)
    private Language language;

    @CreationTimestamp
    private LocalDateTime created;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "word_translation",
            joinColumns = @JoinColumn(name = "base_word_id"),
            inverseJoinColumns = @JoinColumn(name = "translation_word_id")
    )
    private Set<Word> translations;

    public Word(String value) {
        this.value = value;
    }

}
