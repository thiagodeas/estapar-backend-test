package com.thiagoalves.estaparbackendtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thiagoalves.estaparbackendtest.models.Sector;
import com.thiagoalves.estaparbackendtest.models.Spot;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {
    int countBySectorAndOccupiedTrue(Sector sector);
    Spot findByLatAndLng(Double lat, Double lng);
}