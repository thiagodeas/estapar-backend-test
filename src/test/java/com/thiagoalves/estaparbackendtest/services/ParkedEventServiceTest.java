package com.thiagoalves.estaparbackendtest.services;

import com.thiagoalves.estaparbackendtest.dtos.webhook.ParkedEventDTO;
import com.thiagoalves.estaparbackendtest.dtos.webhook.GenericWebhookDTO;
import com.thiagoalves.estaparbackendtest.exceptions.SpotAlreadyOccupiedException;
import com.thiagoalves.estaparbackendtest.exceptions.SpotNotFoundException;
import com.thiagoalves.estaparbackendtest.exceptions.VehicleNotInsideException;
import com.thiagoalves.estaparbackendtest.models.ParkingEvent;
import com.thiagoalves.estaparbackendtest.models.Spot;
import com.thiagoalves.estaparbackendtest.models.enums.ParkingEventStatus;
import com.thiagoalves.estaparbackendtest.repositories.ParkingEventRepository;
import com.thiagoalves.estaparbackendtest.repositories.SpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParkedEventServiceTest {
    @Mock
    private ParkingEventRepository parkingEventRepository;
    @Mock
    private SpotRepository spotRepository;
    @InjectMocks
    private ParkedEventService parkedEventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        parkedEventService = new ParkedEventService(parkingEventRepository, spotRepository);
    }

    @Test
    void process_shouldThrowVehicleNotInsideException_whenEventIsNull() {
        GenericWebhookDTO webhookDTO = new GenericWebhookDTO();
        webhookDTO.license_plate = "ABC1234";
        ParkedEventDTO dto = new ParkedEventDTO(webhookDTO);
        when(parkingEventRepository.findByLicensePlateAndExitTimeIsNull(dto.licensePlate)).thenReturn(null);
        VehicleNotInsideException ex = assertThrows(VehicleNotInsideException.class, () -> parkedEventService.process(dto));
        assertTrue(ex.getMessage().contains("não está dentro"));
    }

    @Test
    void process_shouldThrowSpotNotFoundException_whenSpotIsNull() {
        GenericWebhookDTO webhookDTO = new GenericWebhookDTO();
        webhookDTO.license_plate = "ABC1234";
        webhookDTO.lat = 1.0;
        webhookDTO.lng = 2.0;
        ParkedEventDTO dto = new ParkedEventDTO(webhookDTO);
        ParkingEvent event = mock(ParkingEvent.class);
        when(parkingEventRepository.findByLicensePlateAndExitTimeIsNull(dto.licensePlate)).thenReturn(event);
        when(spotRepository.findByLatAndLng(dto.lat, dto.lng)).thenReturn(null);
        SpotNotFoundException ex = assertThrows(SpotNotFoundException.class, () -> parkedEventService.process(dto));
        assertTrue(ex.getMessage().contains("Nenhuma vaga encontrada"));
    }

    @Test
    void process_shouldThrowSpotAlreadyOccupiedException_whenSpotOccupied() {
        GenericWebhookDTO webhookDTO = new GenericWebhookDTO();
        webhookDTO.license_plate = "ABC1234";
        webhookDTO.lat = 1.0;
        webhookDTO.lng = 2.0;
        ParkedEventDTO dto = new ParkedEventDTO(webhookDTO);
        ParkingEvent event = mock(ParkingEvent.class);
        Spot spot = mock(Spot.class);
        when(parkingEventRepository.findByLicensePlateAndExitTimeIsNull(dto.licensePlate)).thenReturn(event);
        when(spotRepository.findByLatAndLng(dto.lat, dto.lng)).thenReturn(spot);
        when(spot.getOccupied()).thenReturn(true);
        SpotAlreadyOccupiedException ex = assertThrows(SpotAlreadyOccupiedException.class, () -> parkedEventService.process(dto));
        assertTrue(ex.getMessage().contains("já está ocupada"));
    }

    @Test
    void process_shouldSetSpotAndStatus_whenSpotAvailable() {
        GenericWebhookDTO webhookDTO = new GenericWebhookDTO();
        webhookDTO.license_plate = "ABC1234";
        webhookDTO.lat = 1.0;
        webhookDTO.lng = 2.0;
        ParkedEventDTO dto = new ParkedEventDTO(webhookDTO);
        ParkingEvent event = mock(ParkingEvent.class);
        Spot spot = mock(Spot.class);
        when(parkingEventRepository.findByLicensePlateAndExitTimeIsNull(dto.licensePlate)).thenReturn(event);
        when(spotRepository.findByLatAndLng(dto.lat, dto.lng)).thenReturn(spot);
        when(spot.getOccupied()).thenReturn(false);
        parkedEventService.process(dto);
        verify(spot).setOccupied(true);
        verify(spotRepository).save(spot);
        verify(event).setSpot(spot);
        verify(event).setStatus(ParkingEventStatus.PARKED);
        verify(parkingEventRepository).save(event);
    }
}
