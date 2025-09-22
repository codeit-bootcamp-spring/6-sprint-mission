package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)  //잘못된 인자가 전달되었을 때
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);  //400
    }

    @ExceptionHandler(NoSuchElementException.class)  //조회하려는 데이터가 없을 때
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        return new ResponseEntity<String>("Resource not found", HttpStatus.NOT_FOUND);  //404
    }

    @ExceptionHandler(Exception.class)  //모든 예외 처리
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<String>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);  //500
    }
}