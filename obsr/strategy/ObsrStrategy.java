package com.simpledesign.ndms.common.obsr.strategy;

import com.simpledesign.ndms.common.obsr.GbSearchCode;
import com.simpledesign.ndms.common.obsr.ObsrInterfaceResponseDto;
import com.simpledesign.ndms.common.obsr.ObsrInterfaceResultDto;

import java.util.List;

public interface ObsrStrategy<T extends ObsrInterfaceResultDto, S extends ObsrInterfaceResponseDto> {

    List<S> handleAverages(List<T> resultDto, S responseDto, GbSearchCode gbSearchCode);
}
