package com.thiagoalves.estaparbackendtest.services;

import java.math.BigDecimal;
import java.time.Duration;

import org.springframework.stereotype.Service;

import com.thiagoalves.estaparbackendtest.dtos.webhook.ExitEventDTO;
import com.thiagoalves.estaparbackendtest.models.ParkingEvent;
import com.thiagoalves.estaparbackendtest.models.Spot;
import com.thiagoalves.estaparbackendtest.models.enums.ParkingEventStatus;
import com.thiagoalves.estaparbackendtest.repositories.ParkingEventRepository;
import com.thiagoalves.estaparbackendtest.repositories.SpotRepository;

@Service
public class ExitEventService {

    private final ParkingEventRepository parkingEventRepository;
    private final SpotRepository spotRepository;

    public ExitEventService(
            ParkingEventRepository parkingEventRepository,
            SpotRepository spotRepository) {
        this.parkingEventRepository = parkingEventRepository;
        this.spotRepository = spotRepository;
    }

    public void process(ExitEventDTO exitEvent) {

        ParkingEvent event = parkingEventRepository
                .findByLicensePlateAndExitTimeIsNull(exitEvent.licensePlate);

        if (event == null) {
            throw new RuntimeException("EXIT recebido para veículo que não está dentro.");
        }

        event.setExitTime(exitEvent.exitTime);

        Duration duration = Duration.between(event.getEntryTime(), event.getExitTime());
        long totalMinutes = duration.toMinutes();

        BigDecimal amount = BigDecimal.ZERO;

        if (totalMinutes > 30) {

            long paidMinutes = totalMinutes - 30;
            long hours = (long) Math.ceil(paidMinutes / 60.0);

            BigDecimal basePrice = event.getSector().getBasePrice();
            BigDecimal multiplier = event.getDynamicMultiplier();

            amount = basePrice
                    .multiply(BigDecimal.valueOf(hours))
                    .multiply(multiplier);
        }

        event.setAmount(amount);

        Spot spot = event.getSpot();
        if (spot != null) {
            spot.setOccupied(false);
            spotRepository.save(spot);
        }

        event.setStatus(ParkingEventStatus.EXIT);

        parkingEventRepository.save(event);
    }
}
