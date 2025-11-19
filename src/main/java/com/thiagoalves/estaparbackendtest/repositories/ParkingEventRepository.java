package com.thiagoalves.estaparbackendtest.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thiagoalves.estaparbackendtest.models.ParkingEvent;
import com.thiagoalves.estaparbackendtest.models.Sector;

public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long>{
    
    ParkingEvent findByLicensePlateAndExitTimeIsNull(String licensePlate);

    List<ParkingEvent> findBySectorAndExitTimeIsNotNull(Sector sector);

    List<ParkingEvent> findBySector(Sector sector);

}
