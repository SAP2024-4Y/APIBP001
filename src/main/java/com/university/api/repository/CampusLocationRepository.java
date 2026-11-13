package com.university.api.repository;

import com.university.api.model.CampusLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CampusLocationRepository extends JpaRepository<CampusLocation, Long> {
    List<CampusLocation> findByType(String type);

    // Returns all locations; haversine filtering is done in-service for SQLite compatibility
    @Query("SELECT l FROM CampusLocation l")
    List<CampusLocation> findAllLocations();

    @Query("SELECT l FROM CampusLocation l WHERE l.capacity >= :minCapacity")
    List<CampusLocation> findByMinCapacity(@Param("minCapacity") int minCapacity);
}
