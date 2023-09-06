package com.reserve.lab.api.model.dto;

public record ReservationDtoWithError(ReservationDto dto, Integer rowNumber, String error) {
    public ReservationDto getDto() {
        return dto;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }
}
