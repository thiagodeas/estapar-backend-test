package com.thiagoalves.estaparbackendtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thiagoalves.estaparbackendtest.models.Sector;

@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {
    Sector findBySector(String sector);
}
