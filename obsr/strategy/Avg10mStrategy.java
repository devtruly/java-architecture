package com.simpledesign.ndms.common.obsr.strategy;

import com.simpledesign.ndms.common.Utils;
import com.simpledesign.ndms.common.obsr.GbSearchCode;
import com.simpledesign.ndms.common.obsr.ObsrInterfaceResponseDto;
import com.simpledesign.ndms.common.obsr.ObsrInterfaceResultDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Avg10mStrategy<T extends ObsrInterfaceResultDto, S extends ObsrInterfaceResponseDto> implements ObsrStrategy<T, S> {
    @Override
    public List<S> handleAverages(List<T> resultDtoList, S responseDto, GbSearchCode gbSearchCode) {
        List<S> responseDtoList = new ArrayList<>();

        for (T resultDto : resultDtoList) {
            S newResponseDto = (S) responseDto.newInstance();
            newResponseDto.merge(resultDto);
            LocalDateTime localDateTime = LocalDateTime.parse(resultDto.getObsrDttm(), Utils.OBSV_DATE_FORMATTER);
            int minute = (localDateTime.getMinute() / 10) * 10; // 10분 단위로 절삭
            localDateTime = localDateTime.withMinute(minute).withSecond(0).withNano(0);
            newResponseDto.setObsrDttm(
                    localDateTime.format(gbSearchCode.getDateTimeFormatter())
            );
            responseDtoList.add(
                    newResponseDto
            );
        }
        return responseDtoList;
    }
}
