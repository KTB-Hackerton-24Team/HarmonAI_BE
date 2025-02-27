package Hackerton.BE.HarmonAI.controller;

import Hackerton.BE.HarmonAI.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(Exception e) {
        log.error("Error Occurred", e);
        return new ResponseEntity<>(
                ErrorResponseDTO.of(e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
