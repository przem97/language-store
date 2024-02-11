package com.przem7.englishcourseapp.service;

import com.przem7.englishcourseapp.model.orm.SuccessMatch;
import com.przem7.englishcourseapp.repository.SuccessMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SuccessMatchService {

    @Autowired
    private SuccessMatchRepository successMatchRepository;

    public Iterable<SuccessMatch> getAllSuccessMatches() {
        return successMatchRepository.findAll();
    }

    public Optional<SuccessMatch> getSuccessMatchById(Long id) {
        return successMatchRepository.findById(id);
    }

}
