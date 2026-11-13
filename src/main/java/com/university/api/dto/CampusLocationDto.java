package com.university.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampusLocationDto {
    private Long id;
    private String name;
    private String type;
    private String building;
    private String room;
    private Integer capacity;
    private Double latitude;
    private Double longitude;
    private String address;
    private Double distanceKm;
}
