package com.thiagoalves.estaparbackendtest.services;

import com.thiagoalves.estaparbackendtest.dtos.webhook.ExitEventDTO;
import com.thiagoalves.estaparbackendtest.dtos.webhook.GenericWebhookDTO;
import com.thiagoalves.estaparbackendtest.exceptions.VehicleNotInsideException;
import com.thiagoalves.estaparbackendtest.models.ParkingEvent;
import com.thiagoalves.estaparbackendtest.models.Sector;
import com.thiagoalves.estaparbackendtest.models.Spot;
import com.thiagoalves.estaparbackendtest.models.enums.ParkingEventStatus;
import com.thiagoalves.estaparbackendtest.repositories.ParkingEventRepository;
import com.thiagoalves.estaparbackendtest.repositories.SpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExitEventServiceTest {
    @Mock
    private ParkingEventRepository parkingEventRepository;
    @Mock
    private SpotRepository spotRepository;
    @InjectMocks
    private ExitEventService exitEventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exitEventService = new ExitEventService(parkingEventRepository, spotRepository);
    }

    @Test
    void process_shouldThrowVehicleNotInsideException_whenVehicleNotInside() {
        GenericWebhookDTO webhookDTO = new GenericWebhookDTO();
        webhookDTO.license_plate = "ABC1234";
        webhookDTO.exit_time = java.time.OffsetDateTime.now().toString();
        ExitEventDTO dto = new ExitEventDTO(webhookDTO);
        when(parkingEventRepository.findByLicensePlateAndExitTimeIsNull(dto.licensePlate)).thenReturn(null);
        VehicleNotInsideException ex = assertThrows(VehicleNotInsideException.class, () -> exitEventService.process(dto));
        assertEquals("EXIT recebido para veículo que não está dentro.", ex.getMessage());
    }

    @Test
    void process_shouldSetAmountZero_whenDurationIs30MinutesOrLess() {
        GenericWebhookDTO webhookDTO = new GenericWebhookDTO();
        webhookDTO.license_plate = "ABC1234";
        webhookDTO.exit_time = java.time.OffsetDateTime.now().toString();
        ExitEventDTO dto = new ExitEventDTO(webhookDTO);
        ParkingEvent event = mock(ParkingEvent.class);
        when(event.getEntryTime()).thenReturn(dto.exitTime.minusMinutes(30));
        when(event.getExitTime()).thenReturn(dto.exitTime);
        when(parkingEventRepository.findByLicensePlateAndExitTimeIsNull(dto.licensePlate)).thenReturn(event);
        Spot spot = mock(Spot.class);
        when(event.getSpot()).thenReturn(spot);
        when(event.getSector()).thenReturn(mock(Sector.class));
        when(event.getDynamicMultiplier()).thenReturn(BigDecimal.ONE);
        exitEventService.process(dto);
        verify(event).setAmount(BigDecimal.ZERO);
        verify(spot).setOccupied(false);
        verify(spotRepository).save(spot);
        verify(event).setStatus(ParkingEventStatus.EXIT);
        verify(parkingEventRepository).save(event);
    }

    @Test
    void process_shouldCalculateAmount_whenDurationIsGreaterThan30Minutes() {
        GenericWebhookDTO webhookDTO = new GenericWebhookDTO();
        webhookDTO.license_plate = "ABC1234";
        webhookDTO.exit_time = java.time.OffsetDateTime.now().toString();
        ExitEventDTO dto = new ExitEventDTO(webhookDTO);
        ParkingEvent event = mock(ParkingEvent.class);
        when(event.getEntryTime()).thenReturn(dto.exitTime.minusMinutes(90)); // 90 min
        when(event.getExitTime()).thenReturn(dto.exitTime);
        when(parkingEventRepository.findByLicensePlateAndExitTimeIsNull(dto.licensePlate)).thenReturn(event);
        Spot spot = mock(Spot.class);
        when(event.getSpot()).thenReturn(spot);
        Sector sector = mock(Sector.class);
        when(event.getSector()).thenReturn(sector);
        when(sector.getBasePrice()).thenReturn(BigDecimal.valueOf(10));
        when(event.getDynamicMultiplier()).thenReturn(BigDecimal.valueOf(1.2));
        exitEventService.process(dto);
        // 90 - 30 = 60 min => 1 hora paga
        BigDecimal expectedAmount = BigDecimal.valueOf(10).multiply(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(1.2));
        verify(event).setAmount(expectedAmount);
        verify(spot).setOccupied(false);
        verify(spotRepository).save(spot);
        verify(event).setStatus(ParkingEventStatus.EXIT);
        verify(parkingEventRepository).save(event);
    }

    @Test
    void process_shouldHandleNullSpot() {
        GenericWebhookDTO webhookDTO = new GenericWebhookDTO();
        webhookDTO.license_plate = "ABC1234";
        webhookDTO.exit_time = java.time.OffsetDateTime.now().toString();
        ExitEventDTO dto = new ExitEventDTO(webhookDTO);
        ParkingEvent event = mock(ParkingEvent.class);
        when(event.getEntryTime()).thenReturn(dto.exitTime.minusMinutes(60));
        when(event.getExitTime()).thenReturn(dto.exitTime);
        when(parkingEventRepository.findByLicensePlateAndExitTimeIsNull(dto.licensePlate)).thenReturn(event);
        when(event.getSpot()).thenReturn(null);
        Sector sector = mock(Sector.class);
        when(event.getSector()).thenReturn(sector);
        when(sector.getBasePrice()).thenReturn(BigDecimal.valueOf(10));
        when(event.getDynamicMultiplier()).thenReturn(BigDecimal.valueOf(1.0));
        exitEventService.process(dto);
        BigDecimal expectedAmount = BigDecimal.valueOf(10).multiply(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(1.0));
        verify(event).setAmount(expectedAmount);
        verify(event).setStatus(ParkingEventStatus.EXIT);
        verify(parkingEventRepository).save(event);
        // spotRepository.save() não deve ser chamado
        verify(spotRepository, never()).save(any());
    }
}
