package org.npeonelove.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    int statusCode;
    String error;
    String message;
    LocalDateTime timestamp;
}
