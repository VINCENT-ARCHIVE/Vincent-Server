package com.vincent.domain.building.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BuildingResponseDto {

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class BuildingInfo {

    Long buildingId;
    String buildingName;
    String buildingImage;
    String buildingAddress;
  }

}
