package com.vincent.domain.building.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BuildingRequestDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private String name;
        private String address;

        @Min(value = -180, message = "유효한 경도를 입력해 주세요.")
        @Max(value = 180, message = "유효한 경도를 입력해 주세요.")
        private Double longitude;

        @Min(value = -90, message = "유효한 위도를 입력해 주세요.")
        @Max(value = 90, message = "유효한 위도를 입력해 주세요.")
        private Double latitude;
    }
}
