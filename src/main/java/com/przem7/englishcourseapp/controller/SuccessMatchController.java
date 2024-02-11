package com.przem7.englishcourseapp.controller;

import com.przem7.englishcourseapp.model.orm.SuccessMatch;
import com.przem7.englishcourseapp.service.SuccessMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class SuccessMatchController {

    @Autowired
    private SuccessMatchService successMatchService;

    @GetMapping("/successMatches")
    public ResponseEntity<Iterable<SuccessMatch>> getFailureMatches() {
        return ResponseEntity.ok(successMatchService.getAllSuccessMatches());
    }

    @GetMapping("/successMatches/{successMatchId}")
    public ResponseEntity<SuccessMatch> getSuccessMatchById(@PathVariable("successMatchId") Long id) {
        Optional<SuccessMatch> optional = successMatchService.getSuccessMatchById(id);
        return optional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }
}
