package com.university.api.controller;

import com.university.api.dto.CampusLocationDto;
import com.university.api.dto.GeoJsonFeatureCollection;
import com.university.api.service.CampusLocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Tag(name = "Campus Locations")
@SecurityRequirement(name = "bearerAuth")
public class CampusLocationController {

    private final CampusLocationService locationService;

    @GetMapping
    @Operation(summary = "List all campus locations")
    public ResponseEntity<List<CampusLocationDto>> listLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a campus location by ID")
    public ResponseEntity<CampusLocationDto> getLocation(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocation(id));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find locations near a coordinate (GeoJSON)")
    public ResponseEntity<GeoJsonFeatureCollection> nearby(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "1.0") double radius_km) {
        return ResponseEntity.ok(locationService.getNearbyLocations(latitude, longitude, radius_km));
    }
}
