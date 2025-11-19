package com.thiagoalves.estaparbackendtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thiagoalves.estaparbackendtest.models.Spot;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {
    
}
