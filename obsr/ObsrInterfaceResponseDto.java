package com.simpledesign.ndms.common.obsr;

public interface ObsrInterfaceResponseDto<S, T extends ObsrInterfaceResultDto> {
    void setObsrDttm(String obsrDttm);
    S merge(T resultDto);
    S newInstance();
}
