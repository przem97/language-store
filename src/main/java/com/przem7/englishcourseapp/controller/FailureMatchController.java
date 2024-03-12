package com.przem7.englishcourseapp.controller;

import com.przem7.englishcourseapp.model.orm.FailureMatch;
import com.przem7.englishcourseapp.service.FailureMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class FailureMatchController {

    @Autowired
    private FailureMatchService failureMatchService;

    @GetMapping("/failureMatches")
    public ResponseEntity<Iterable<FailureMatch>> getFailureMatches() {
        return ResponseEntity.ok(failureMatchService.getAllFailureMatches());
    }

    @GetMapping("/failureMatches/{failureMatchId}")
    public ResponseEntity<FailureMatch> getFailureMatchById(@PathVariable("failureMatchId") Long id) {
        Optional<FailureMatch> optional = failureMatchService.getFailureMatchById(id);
        return optional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }
}
