package com.thiagoalves.estaparbackendtest.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.thiagoalves.estaparbackendtest.dtos.revenue.RevenueRequestDTO;
import com.thiagoalves.estaparbackendtest.dtos.revenue.RevenueResponseDTO;
import com.thiagoalves.estaparbackendtest.exceptions.SectorNotFoundException;
import com.thiagoalves.estaparbackendtest.models.ParkingEvent;
import com.thiagoalves.estaparbackendtest.models.Sector;
import com.thiagoalves.estaparbackendtest.repositories.ParkingEventRepository;
import com.thiagoalves.estaparbackendtest.repositories.SectorRepository;

@Service
public class RevenueService {

    private final SectorRepository sectorRepository;
    private final ParkingEventRepository eventRepository;

    public RevenueService(
            SectorRepository sectorRepository,
            ParkingEventRepository eventRepository) {
        this.sectorRepository = sectorRepository;
        this.eventRepository = eventRepository;
    }

    public RevenueResponseDTO calculate(RevenueRequestDTO request) {

        LocalDate targetDate = LocalDate.parse(request.date);

        Sector sector = sectorRepository.findBySector(request.sector);
        if (sector == null) {
            throw new SectorNotFoundException("Setor inexistente.");
        }

        List<ParkingEvent> events =
                eventRepository.findBySectorAndExitTimeIsNotNull(sector);

        BigDecimal total = events.stream()
                .filter(ev -> ev.getExitTime().toLocalDate().equals(targetDate))
                .map(ev -> ev.getAmount() == null ? BigDecimal.ZERO : ev.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RevenueResponseDTO(
                total,
                targetDate.atStartOfDay().toString()
        );
    }
}
