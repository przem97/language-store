package com.przem7.englishcourseapp.exception.advice;

import com.przem7.englishcourseapp.exception.EnglishCourseStoreException;
import com.przem7.englishcourseapp.exception.word.WordNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class EnglishCourseStoreExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { EnglishCourseStoreException.class })
    protected ResponseEntity<Object> handleEnglishCourseStoreException(EnglishCourseStoreException exception, WebRequest webRequest) {
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
    @Override
    @Nullable
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problemDetail = ex.updateAndGetBody(this.getMessageSource(), LocaleContextHolder.getLocale());
        Map<String, Object> properties = problemDetail.getProperties() == null ? new HashMap<>() : problemDetail.getProperties();

        for (int i = 0; i < ex.getAllValidationResults().size(); i += 1) {
            String parameterName = ex.getAllValidationResults().get(i).getMethodParameter().getParameterName();
            Object argument = ex.getAllValidationResults().get(i).getArgument();

            String key = "error_" + i;
            properties.put(key, ex.getAllValidationResults().get(i)
                    .getResolvableErrors()
                    .stream()
                    .map(x -> parameterName + " " + x.getDefaultMessage() + ". Actual value=" + argument)
                    .collect(Collectors.toList()))
            ;
        }

        problemDetail.setProperties(properties);

        return this.handleExceptionInternal(ex, problemDetail, headers, HttpStatus.BAD_REQUEST, request);
    }
}
