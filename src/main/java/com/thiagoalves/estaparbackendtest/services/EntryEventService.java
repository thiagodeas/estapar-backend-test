package com.thiagoalves.estaparbackendtest.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.thiagoalves.estaparbackendtest.dtos.webhook.EntryEventDTO;
import com.thiagoalves.estaparbackendtest.exceptions.NoAvailableSectorException;
import com.thiagoalves.estaparbackendtest.exceptions.VehicleAlreadyInsideException;
import com.thiagoalves.estaparbackendtest.models.ParkingEvent;
import com.thiagoalves.estaparbackendtest.models.Sector;
import com.thiagoalves.estaparbackendtest.models.enums.ParkingEventStatus;
import com.thiagoalves.estaparbackendtest.repositories.ParkingEventRepository;
import com.thiagoalves.estaparbackendtest.repositories.SectorRepository;

@Service
public class EntryEventService {

    private final ParkingEventRepository parkingEventRepository;
    private final SectorRepository sectorRepository;

    public EntryEventService(
            ParkingEventRepository parkingEventRepository,
            SectorRepository sectorRepository) {
        this.parkingEventRepository = parkingEventRepository;
        this.sectorRepository = sectorRepository;
    }

    public void process(EntryEventDTO entryEvent) {

        ParkingEvent existing = parkingEventRepository
                .findByLicensePlateAndExitTimeIsNull(entryEvent.licensePlate);

        if (existing != null) {
            throw new VehicleAlreadyInsideException("Veículo já está dentro do estacionamento.");
        }

        Sector chosenSector = chooseSector();
        if (chosenSector == null) {
            throw new NoAvailableSectorException("Nenhum setor disponível no momento.");
        }

        int currentCars = parkingEventRepository.countBySectorAndExitTimeIsNull(chosenSector);
        double ratio = (double) currentCars / chosenSector.getMaxCapacity();

        BigDecimal multiplier = getDynamicMultiplier(ratio);

        ParkingEvent event = new ParkingEvent(
                entryEvent.licensePlate,
                entryEvent.entryTime,
                chosenSector,
                multiplier,
                ParkingEventStatus.ENTRY
        );

        parkingEventRepository.save(event);
    }

    private Sector chooseSector() {
        List<Sector> sectors = sectorRepository.findAll();

        for (Sector s : sectors) {
            int currentCars = parkingEventRepository.countBySectorAndExitTimeIsNull(s);
            if (currentCars < s.getMaxCapacity()) {
                return s; 
            }
        }

        return null; 
    }

    private BigDecimal getDynamicMultiplier(double ratio) {
        if (ratio < 0.25) return BigDecimal.valueOf(0.9);
        if (ratio < 0.50) return BigDecimal.valueOf(1.0);
        if (ratio < 0.75) return BigDecimal.valueOf(1.1);
        return BigDecimal.valueOf(1.25);
    }
}
