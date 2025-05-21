package com.orderservice.util;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class CommonUtils {

    public static <T> ResponseEntity<T> getResponseEntity(T body, HttpStatusCode status) {
        return new ResponseEntity<>(body, status);
    }
}
