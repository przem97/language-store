package com.przem7.englishcourseapp.service;

import com.przem7.englishcourseapp.model.orm.FailureMatch;
import com.przem7.englishcourseapp.repository.FailureMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FailureMatchService {

    @Autowired
    private FailureMatchRepository failureMatchRepository;

    public Iterable<FailureMatch> getAllFailureMatches() {
        return failureMatchRepository.findAll();
    }

    public Optional<FailureMatch> getFailureMatchById(Long id) {
        return failureMatchRepository.findById(id);
    }

}
