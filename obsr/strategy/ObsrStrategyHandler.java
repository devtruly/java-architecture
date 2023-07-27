package com.simpledesign.ndms.common.obsr.strategy;

import com.simpledesign.ndms.common.Utils;
import com.simpledesign.ndms.common.obsr.GbSearchCode;
import com.simpledesign.ndms.common.obsr.ObsrInterfaceResponseDto;
import com.simpledesign.ndms.common.obsr.ObsrInterfaceResultDto;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ObsrStrategyHandler <T extends ObsrInterfaceResultDto, S extends ObsrInterfaceResponseDto> {
    private GbSearchCode gbSearchCode;

    public List<S> getResponseDtoList(List<T> resultDto, S responseDto) {
        ObsrStrategy<T, S> obsrStrategy = null;

        if (Utils.DEFAUILT_GB_SEARCH.contains(gbSearchCode)) {
            obsrStrategy = new AvgDefaultStrategy<>();
        } else {
            if (gbSearchCode.equals(GbSearchCode.avg1s)) {
                obsrStrategy = new Avg1sStrategy<>();
            }
            if (gbSearchCode.equals(GbSearchCode.avg1m)) {
                obsrStrategy = new Avg1mStrategy<>();
            }
            if (gbSearchCode.equals(GbSearchCode.avg10m)) {
                obsrStrategy = new Avg10mStrategy<>();
            }
        }

        return obsrStrategy.handleAverages(resultDto, (S)responseDto.newInstance(), gbSearchCode);
    }
}
