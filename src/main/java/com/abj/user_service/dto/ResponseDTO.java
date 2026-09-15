package com.abj.user_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> {
    private boolean success;
    private T result;
    private ErrorDetails error;

    public static <T> ResponseDTO<T> success(T result) {
        return new ResponseDTO<>(true, result, null);
    }

    public static <T> ResponseDTO<T> failure(ErrorDetails error) {
        return new ResponseDTO<>(false, null, error);
    }
}