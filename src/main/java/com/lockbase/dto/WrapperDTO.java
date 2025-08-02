package com.lockbase.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WrapperDTO<T> {

    private Boolean success;
    private T respObject;
}