package com.example.storereservation.global.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private ErrorCode errorCode;
    private String errorMessage;

    public ErrorResponse(ErrorCode errorCode){
        this.statusCode = errorCode.getStatusCode();;
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}