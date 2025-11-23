package com.thiagoalves.estaparbackendtest.services;

import org.springframework.stereotype.Service;

import com.thiagoalves.estaparbackendtest.dtos.webhook.ParkedEventDTO;
import com.thiagoalves.estaparbackendtest.exceptions.SpotAlreadyOccupiedException;
import com.thiagoalves.estaparbackendtest.exceptions.SpotNotFoundException;
import com.thiagoalves.estaparbackendtest.exceptions.VehicleNotInsideException;
import com.thiagoalves.estaparbackendtest.models.ParkingEvent;
import com.thiagoalves.estaparbackendtest.models.Spot;
import com.thiagoalves.estaparbackendtest.models.enums.ParkingEventStatus;
import com.thiagoalves.estaparbackendtest.repositories.ParkingEventRepository;
import com.thiagoalves.estaparbackendtest.repositories.SpotRepository;

@Service
public class ParkedEventService {

    private final ParkingEventRepository parkingEventRepository;
    private final SpotRepository spotRepository;

    public ParkedEventService(
            ParkingEventRepository parkingEventRepository,
            SpotRepository spotRepository) {
        this.parkingEventRepository = parkingEventRepository;
        this.spotRepository = spotRepository;
    }

    public void process(ParkedEventDTO parkedEvent) {

        ParkingEvent event = parkingEventRepository
                .findByLicensePlateAndExitTimeIsNull(parkedEvent.licensePlate);

        if (event == null) {
            throw new VehicleNotInsideException("PARKED recebido mas o veículo " + parkedEvent.licensePlate + " não está dentro.");
        }

        Spot spot = spotRepository
                .findByLatAndLng(parkedEvent.lat, parkedEvent.lng);

        if (spot == null) {
            throw new SpotNotFoundException("Nenhuma vaga encontrada para as coordenadas informadas.");
        }

        if (Boolean.TRUE.equals(spot.getOccupied())) {
            throw new SpotAlreadyOccupiedException(
                "A vaga " + spot.getId() + " já está ocupada."
            );
        }

        spot.setOccupied(true);
        spotRepository.save(spot);

        event.setSpot(spot);

        event.setStatus(ParkingEventStatus.PARKED);

        parkingEventRepository.save(event);
    }
}
