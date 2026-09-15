package com.abj.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String errorCode;
}
