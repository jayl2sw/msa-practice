package com.example.userservice.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(404, "U001", "회원 정보를 찾을 수 없습니다."),
    INVALID_PARAMETER(400, "C001", "잘못된 요청입니다.");


    private final int status;
    private final String code;
    private final String message;


}
