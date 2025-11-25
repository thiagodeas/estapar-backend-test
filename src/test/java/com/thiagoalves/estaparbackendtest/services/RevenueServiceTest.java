package com.thiagoalves.estaparbackendtest.services;

import com.thiagoalves.estaparbackendtest.dtos.revenue.RevenueRequestDTO;
import com.thiagoalves.estaparbackendtest.dtos.revenue.RevenueResponseDTO;
import com.thiagoalves.estaparbackendtest.exceptions.SectorNotFoundException;
import com.thiagoalves.estaparbackendtest.models.ParkingEvent;
import com.thiagoalves.estaparbackendtest.models.Sector;
import com.thiagoalves.estaparbackendtest.repositories.ParkingEventRepository;
import com.thiagoalves.estaparbackendtest.repositories.SectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RevenueServiceTest {
    @Mock
    private SectorRepository sectorRepository;
    @Mock
    private ParkingEventRepository eventRepository;
    @InjectMocks
    private RevenueService revenueService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        revenueService = new RevenueService(sectorRepository, eventRepository);
    }

    @Test
    void calculate_shouldThrowSectorNotFoundException_whenSectorIsNull() {
        RevenueRequestDTO request = new RevenueRequestDTO();
        request.sector = "A";
        request.date = "2025-11-25";
        when(sectorRepository.findBySector(request.sector)).thenReturn(null);
        SectorNotFoundException ex = assertThrows(SectorNotFoundException.class, () -> revenueService.calculate(request));
        assertEquals("Setor inexistente.", ex.getMessage());
    }

    @Test
    void calculate_shouldReturnZero_whenNoEvents() {
        RevenueRequestDTO request = new RevenueRequestDTO();
        request.sector = "A";
        request.date = "2025-11-25";
        Sector sector = mock(Sector.class);
        when(sectorRepository.findBySector(request.sector)).thenReturn(sector);
        when(eventRepository.findBySectorAndExitTimeIsNotNull(sector)).thenReturn(Collections.emptyList());
        RevenueResponseDTO response = revenueService.calculate(request);
        assertEquals(BigDecimal.ZERO, response.amount);
        assertEquals("2025-11-25T00:00", response.timestamp);
    }

    @Test
    void calculate_shouldSumAmounts_whenEventsExist() {
        RevenueRequestDTO request = new RevenueRequestDTO();
        request.sector = "A";
        request.date = "2025-11-25";
        Sector sector = mock(Sector.class);
        when(sectorRepository.findBySector(request.sector)).thenReturn(sector);
        ParkingEvent event1 = mock(ParkingEvent.class);
        ParkingEvent event2 = mock(ParkingEvent.class);
        when(event1.getExitTime()).thenReturn(LocalDateTime.of(2025, 11, 25, 10, 0));
        when(event2.getExitTime()).thenReturn(LocalDateTime.of(2025, 11, 25, 15, 0));
        when(event1.getAmount()).thenReturn(BigDecimal.valueOf(10));
        when(event2.getAmount()).thenReturn(BigDecimal.valueOf(20));
        List<ParkingEvent> events = Arrays.asList(event1, event2);
        when(eventRepository.findBySectorAndExitTimeIsNotNull(sector)).thenReturn(events);
        RevenueResponseDTO response = revenueService.calculate(request);
        assertEquals(BigDecimal.valueOf(30), response.amount);
        assertEquals("2025-11-25T00:00", response.timestamp);
    }

    @Test
    void calculate_shouldTreatNullAmountAsZero() {
        RevenueRequestDTO request = new RevenueRequestDTO();
        request.sector = "A";
        request.date = "2025-11-25";
        Sector sector = mock(Sector.class);
        when(sectorRepository.findBySector(request.sector)).thenReturn(sector);
        ParkingEvent event1 = mock(ParkingEvent.class);
        when(event1.getExitTime()).thenReturn(LocalDateTime.of(2025, 11, 25, 10, 0));
        when(event1.getAmount()).thenReturn(null);
        List<ParkingEvent> events = Arrays.asList(event1);
        when(eventRepository.findBySectorAndExitTimeIsNotNull(sector)).thenReturn(events);
        RevenueResponseDTO response = revenueService.calculate(request);
        assertEquals(BigDecimal.ZERO, response.amount);
        assertEquals("2025-11-25T00:00", response.timestamp);
    }
}
