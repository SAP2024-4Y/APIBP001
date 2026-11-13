package com.university.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoJsonFeatureCollection {
    private String type = "FeatureCollection";
    private List<Feature> features;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Feature {
        private String type = "Feature";
        private Geometry geometry;
        private Map<String, Object> properties;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Geometry {
        private String type = "Point";
        private double[] coordinates;
    }
}
