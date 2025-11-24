package com.thiagoalves.estaparbackendtest.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thiagoalves.estaparbackendtest.models.ParkingEvent;
import com.thiagoalves.estaparbackendtest.models.Sector;

@Repository
public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long>{
    
    ParkingEvent findByLicensePlateAndExitTimeIsNull(String licensePlate);

    List<ParkingEvent> findBySectorAndExitTimeIsNotNull(Sector sector);

    List<ParkingEvent> findBySector(Sector sector);

    int countBySectorAndExitTimeIsNull(Sector sector);
}
