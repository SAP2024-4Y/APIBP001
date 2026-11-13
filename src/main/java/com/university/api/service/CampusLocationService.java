package com.university.api.service;

import com.university.api.dto.CampusLocationDto;
import com.university.api.dto.GeoJsonFeatureCollection;
import com.university.api.model.CampusLocation;
import com.university.api.repository.CampusLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampusLocationService {

    private final CampusLocationRepository locationRepository;

    public List<CampusLocationDto> getAllLocations() {
        return locationRepository.findAll().stream().map(l -> toDto(l, null)).collect(Collectors.toList());
    }

    public CampusLocationDto getLocation(Long id) {
        return locationRepository.findById(id).map(l -> toDto(l, null))
                .orElseThrow(() -> new com.university.api.exception.ResourceNotFoundException("CampusLocation", id));
    }

    public GeoJsonFeatureCollection getNearbyLocations(double latitude, double longitude, double radiusKm) {
        List<CampusLocation> all = locationRepository.findAllLocations();

        List<GeoJsonFeatureCollection.Feature> features = all.stream()
                .filter(loc -> loc.getLatitude() != null && loc.getLongitude() != null)
                .map(loc -> {
                    double dist = haversine(latitude, longitude, loc.getLatitude(), loc.getLongitude());
                    return Map.entry(loc, dist);
                })
                .filter(e -> e.getValue() <= radiusKm)
                .map(e -> {
                    CampusLocation loc = e.getKey();
                    double dist = e.getValue();
                    Map<String, Object> props = new HashMap<>();
                    props.put("id", loc.getId());
                    props.put("name", loc.getName());
                    props.put("type", loc.getType());
                    props.put("building", loc.getBuilding());
                    props.put("room", loc.getRoom());
                    props.put("capacity", loc.getCapacity());
                    props.put("address", loc.getAddress());
                    props.put("distance_km", Math.round(dist * 1000.0) / 1000.0);

                    return GeoJsonFeatureCollection.Feature.builder()
                            .type("Feature")
                            .geometry(GeoJsonFeatureCollection.Geometry.builder()
                                    .type("Point")
                                    .coordinates(new double[]{loc.getLongitude(), loc.getLatitude()})
                                    .build())
                            .properties(props)
                            .build();
                })
                .collect(Collectors.toList());

        return GeoJsonFeatureCollection.builder()
                .type("FeatureCollection")
                .features(features)
                .build();
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private CampusLocationDto toDto(CampusLocation loc, Double distanceKm) {
        return CampusLocationDto.builder()
                .id(loc.getId())
                .name(loc.getName())
                .type(loc.getType())
                .building(loc.getBuilding())
                .room(loc.getRoom())
                .capacity(loc.getCapacity())
                .latitude(loc.getLatitude())
                .longitude(loc.getLongitude())
                .address(loc.getAddress())
                .distanceKm(distanceKm)
                .build();
    }
}
