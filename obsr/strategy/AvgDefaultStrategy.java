package com.simpledesign.ndms.common.obsr.strategy;

import com.simpledesign.ndms.common.Utils;
import com.simpledesign.ndms.common.obsr.GbSearchCode;
import com.simpledesign.ndms.common.obsr.ObsrInterfaceResponseDto;
import com.simpledesign.ndms.common.obsr.ObsrInterfaceResultDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AvgDefaultStrategy<T extends ObsrInterfaceResultDto, S extends ObsrInterfaceResponseDto> implements ObsrStrategy<T, S> {
    @Override
    public List<S> handleAverages(List<T> resultDtoList, S responseDto, GbSearchCode gbSearchCode) {
        List<S> responseDtoList = new ArrayList<>();

        for (T resultDto : resultDtoList) {
            S newResponseDto = (S) responseDto.newInstance();
            newResponseDto.merge(resultDto);
            LocalDateTime localDateTime = LocalDateTime.parse(resultDto.getObsrDttm(), Utils.OBSV_DATE_FORMATTER);
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
