package com.simpledesign.ndms.common.obsr;

import lombok.Getter;

import java.time.format.DateTimeFormatter;
//import org.springframework.http.HttpStatus;

public enum GbSearchCode {
    avg1s("1초평균", DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
    avg1m("1분평균", DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
    avg10m("10분평균", DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
    avg1h("1시간평균", DateTimeFormatter.ofPattern("yyyyMMddHH")),
    avg1d("1일평균", DateTimeFormatter.ofPattern("yyyyMMdd")),
    raw("로우데이터", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

    @Getter
    private String message;
    @Getter
    private DateTimeFormatter dateTimeFormatter;

    GbSearchCode(String message, DateTimeFormatter dateTimeFormatter) {
        this.message = message;
        this.dateTimeFormatter = dateTimeFormatter;
    }

}
