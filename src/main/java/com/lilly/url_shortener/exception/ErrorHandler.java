package com.lilly.url_shortener.exception;

import com.lilly.url_shortener.dtos.UrlDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice // Tells Spring: "Watch all controllers for exceptions"
public class ErrorHandler {

    // Catch NoSuchElementException specifically
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<UrlDto.Error> handleNotFound(NoSuchElementException ex){
        UrlDto.Error error = new UrlDto.Error(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
