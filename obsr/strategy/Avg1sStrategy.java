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

public class Avg1sStrategy<T extends ObsrInterfaceResultDto, S extends ObsrInterfaceResponseDto> implements ObsrStrategy<T, S> {
    @Override
    public List<S> handleAverages(List<T> resultDtoList, S responseDto, GbSearchCode gbSearchCode) {
        List<S> responseDtoList = new ArrayList<>();
        T resultDtoBefore = null;
        for (T resultDto : resultDtoList) {
            if (resultDtoBefore == null) {
                resultDtoBefore = resultDto;
            } else {
                LocalDateTime dateTimeBefore = LocalDateTime.parse(resultDtoBefore.getObsrDttm(), Utils.OBSV_DATE_FORMATTER);
                LocalDateTime dateTimeCurrent = LocalDateTime.parse(resultDto.getObsrDttm(), Utils.OBSV_DATE_FORMATTER);
                Duration minutesBetween = Duration.between(dateTimeBefore, dateTimeCurrent);

                LocalDateTime start = dateTimeBefore.truncatedTo(ChronoUnit.MINUTES);
                LocalDateTime end = dateTimeCurrent.truncatedTo(ChronoUnit.MINUTES);

                while (!start.equals(end)) {
                    LocalDateTime startTime = start;
                    LocalDateTime endTime = start.plusMinutes(1);

                    while (!startTime.equals(endTime)) {
                        S newResponseDto = (S) responseDto.newInstance();
                        newResponseDto.merge(resultDtoBefore);
                        newResponseDto.setObsrDttm(startTime.format(gbSearchCode.getDateTimeFormatter()));
                        responseDtoList.add(newResponseDto);

                        startTime = startTime.plusSeconds(1);
                    }

                    start = start.plus(1, ChronoUnit.MINUTES); // 1분씩 증가
                }

                resultDtoBefore = resultDto;
            }
        }

        LocalDateTime dateTimeBefore = LocalDateTime.parse(resultDtoBefore.getObsrDttm(), Utils.OBSV_DATE_FORMATTER);
//        LocalDateTime dateTimeCurrent = LocalDateTime.parse(resultDtoBefore.getObsrDttm(), Utils.OBSV_DATE_FORMATTER);

        LocalDateTime startTime = dateTimeBefore.truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime endTime = startTime.plusMinutes(1);
//        LocalDateTime endTime = dateTimeCurrent;

        while (!startTime.equals(endTime)) {
            S newResponseDto = (S) responseDto.newInstance();
            newResponseDto.merge(resultDtoBefore);
            newResponseDto.setObsrDttm(startTime.format(gbSearchCode.getDateTimeFormatter()));
            responseDtoList.add(newResponseDto);

            startTime = startTime.plusSeconds(1);
        }

        return responseDtoList;
    }
}
