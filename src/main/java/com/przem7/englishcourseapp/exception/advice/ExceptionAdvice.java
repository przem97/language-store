package com.przem7.englishcourseapp.exception.advice;

import com.przem7.englishcourseapp.exception.WordAlreadyExistsException;
import com.przem7.englishcourseapp.exception.WordNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    public static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(value = {
        WordNotFoundException.class,
        WordAlreadyExistsException.class
    })
    public ModelAndView handle(HttpServletRequest req, Exception t) throws Exception {
        log.info("handling " + t.getClass().getSimpleName() + " exception...");

        if (AnnotationUtils.findAnnotation(t.getClass(), ResponseStatus.class) != null) {
            throw t;
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", t);
        mav.addObject("url", req.getRequestURL());
        mav.addObject("code", HttpStatus.NOT_FOUND.value());

        mav.setViewName(DEFAULT_ERROR_VIEW);

        return mav;
    }
}
