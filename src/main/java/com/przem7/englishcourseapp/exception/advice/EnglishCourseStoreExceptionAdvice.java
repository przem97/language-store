package com.przem7.englishcourseapp.exception.advice;

import com.przem7.englishcourseapp.exception.EnglishCourseStoreException;
import com.przem7.englishcourseapp.exception.word.WordNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class EnglishCourseStoreExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { EnglishCourseStoreException.class })
    protected ResponseEntity<Object> handleConflict(Exception exception, WebRequest webRequest) {
        log.info("handling " + exception.getClass().getSimpleName() + " exception...");

        if (exception instanceof WordNotFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("");

        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("");


//        return handleExceptionInternal(exception, "{\"message\": \"" + exception.getMessage() + "\"}",
//                new HttpHeaders(), HttpStatus.CONFLICT, webRequest);
    }
}
