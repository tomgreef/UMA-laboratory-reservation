package com.reserve.lab.api.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto {
    public ResponseDto(Object data, Object error) {
        this.data = data;
        this.error = error;
    }

    private Object data;
    private Object error;
}
