package com.supermarket.checkout.common.exception.dto;

import lombok.Data;

@Data
public class ResponseDto<T> {
    private boolean success = true;
    private String code;
    private String message;
    private T content;
}
