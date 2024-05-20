package com.przem7.englishcourseapp.exception.advice;

import com.przem7.englishcourseapp.exception.EnglishCourseStoreException;
import com.przem7.englishcourseapp.exception.word.WordAlreadyExistsException;
import com.przem7.englishcourseapp.exception.word.WordNotFoundException;
import com.przem7.englishcourseapp.model.dto.error.ParamErrorDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class EnglishCourseStoreExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { EnglishCourseStoreException.class })
    protected ResponseEntity<Object> handleEnglishCourseStoreException(EnglishCourseStoreException exception,
                                                                       WebRequest webRequest) {
        if (exception instanceof WordNotFoundException wordNotFoundException) {
            return this.handleWordNotFoundException(wordNotFoundException, webRequest);
        } else if (exception instanceof WordAlreadyExistsException wordAlreadyExistsException) {
            return this.handleWordAlreadyExistsException(wordAlreadyExistsException, webRequest);
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return handleExceptionInternal(exception, problemDetail,
                new HttpHeaders(), HttpStatus.OK, webRequest);
    }
    @Override
    @Nullable
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problemDetail = ex.updateAndGetBody(this.getMessageSource(), LocaleContextHolder.getLocale());
        Map<String, Object> properties = problemDetail.getProperties() == null ? new HashMap<>() : problemDetail.getProperties();

        for (int i = 0; i < ex.getAllValidationResults().size(); i += 1) {
            String parameterName = ex.getAllValidationResults().get(i).getMethodParameter().getParameterName();
            Object argument = ex.getAllValidationResults().get(i).getArgument();

            properties.put(parameterName, ex.getAllValidationResults().get(i)
                    .getResolvableErrors()
                    .stream()
                    .map(x -> new ParamErrorDetail(x.getDefaultMessage(), argument == null ? "null" : argument.toString()))
                    .collect(Collectors.toList()))
            ;
        }
        HttpStatus responseStatus = HttpStatus.OK;

        problemDetail.setProperties(properties);
        problemDetail.setStatus(responseStatus);

        return this.handleExceptionInternal(ex, problemDetail, headers, responseStatus, request);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problemDetail = ex.updateAndGetBody(this.getMessageSource(), LocaleContextHolder.getLocale());
        Map<String, Object> properties = problemDetail.getProperties() == null ? new HashMap<>() : problemDetail.getProperties();

        Map<String, List<ParamErrorDetail>> paramErrorDetails = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            List<ParamErrorDetail> value = paramErrorDetails.getOrDefault(fieldError.getField(), new ArrayList<>());
            value.add(new ParamErrorDetail(fieldError.getDefaultMessage(),
                        fieldError.getRejectedValue() == null ? "null" : fieldError.getRejectedValue().toString()));

            paramErrorDetails.putIfAbsent(fieldError.getField(), value);
        }

        properties.putAll(paramErrorDetails);

        HttpStatus responseStatus = HttpStatus.OK;

        problemDetail.setProperties(properties);
        problemDetail.setStatus(responseStatus);

        return this.handleExceptionInternal(ex, problemDetail, headers, responseStatus, request);
    }

    private ResponseEntity<Object> handleWordNotFoundException(WordNotFoundException exception,
                                                               WebRequest webRequest) {
        HttpStatus responseStatus = HttpStatus.OK;

        ProblemDetail problemDetail = this.createProblemDetail(exception,
                responseStatus,
                exception.getMessage(),
                null,
                null,
                webRequest);

        return this.handleExceptionInternal(exception, problemDetail, new HttpHeaders(), responseStatus, webRequest);
    }

    private ResponseEntity<Object> handleWordAlreadyExistsException(WordAlreadyExistsException exception,
                                                                    WebRequest webRequest) {
        HttpStatus responseStatus = HttpStatus.OK;

        ProblemDetail problemDetail = this.createProblemDetail(exception,
                responseStatus,
                exception.getMessage(),
                null,
                null,
                webRequest);

        return this.handleExceptionInternal(exception, problemDetail, new HttpHeaders(), responseStatus, webRequest);
    }
}
