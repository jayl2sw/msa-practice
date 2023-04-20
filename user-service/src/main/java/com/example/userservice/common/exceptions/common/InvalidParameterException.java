package com.example.userservice.common.exceptions.common;

import com.example.userservice.common.exceptions.CustomException;
import com.example.userservice.common.exceptions.ErrorCode;
import lombok.Getter;
import org.springframework.validation.Errors;

@Getter
public class InvalidParameterException extends CustomException {
    private final Errors errors;

    public InvalidParameterException(Errors errors){
        super(ErrorCode.INVALID_PARAMETER);
        this.errors = errors;
    }
}