package com.thiagoalves.estaparbackendtest.services;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.thiagoalves.estaparbackendtest.dtos.webhook.EntryEventDTO;
import com.thiagoalves.estaparbackendtest.dtos.webhook.GenericWebhookDTO;
import com.thiagoalves.estaparbackendtest.exceptions.NoAvailableSectorException;
import com.thiagoalves.estaparbackendtest.exceptions.VehicleAlreadyInsideException;
import com.thiagoalves.estaparbackendtest.models.ParkingEvent;
import com.thiagoalves.estaparbackendtest.models.Sector;
import com.thiagoalves.estaparbackendtest.models.enums.ParkingEventStatus;
import com.thiagoalves.estaparbackendtest.repositories.ParkingEventRepository;
import com.thiagoalves.estaparbackendtest.repositories.SectorRepository;

class EntryEventServiceTest {
    @Mock
    private ParkingEventRepository parkingEventRepository;
    @Mock
    private SectorRepository sectorRepository;
    @InjectMocks
    private EntryEventService entryEventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        entryEventService = new EntryEventService(parkingEventRepository, sectorRepository);
    }

    @Test
    void process_shouldThrowVehicleAlreadyInsideException_whenVehicleIsAlreadyInside() {
        GenericWebhookDTO webhookDTO = new GenericWebhookDTO();
        webhookDTO.license_plate = "ABC1234";
        webhookDTO.entry_time = java.time.OffsetDateTime.now().toString();
        EntryEventDTO dto = new EntryEventDTO(webhookDTO);
        when(parkingEventRepository.findByLicensePlateAndExitTimeIsNull(dto.licensePlate)).thenReturn(new ParkingEvent());
        VehicleAlreadyInsideException ex = assertThrows(VehicleAlreadyInsideException.class, () -> entryEventService.process(dto));
        assertEquals("Veículo já está dentro do estacionamento.", ex.getMessage());
    }

    @Test
    void process_shouldThrowNoAvailableSectorException_whenNoSectorAvailable() {
        GenericWebhookDTO webhookDTO = new GenericWebhookDTO();
        webhookDTO.license_plate = "ABC1234";
        webhookDTO.entry_time = java.time.OffsetDateTime.now().toString();
        EntryEventDTO dto = new EntryEventDTO(webhookDTO);
        when(parkingEventRepository.findByLicensePlateAndExitTimeIsNull(dto.licensePlate)).thenReturn(null);
        when(sectorRepository.findAll()).thenReturn(Collections.emptyList());
        NoAvailableSectorException ex = assertThrows(NoAvailableSectorException.class, () -> entryEventService.process(dto));
        assertEquals("Nenhum setor disponível no momento.", ex.getMessage());
    }

    @Test
    void process_shouldSaveParkingEvent_whenSectorAvailable() {
        GenericWebhookDTO webhookDTO = new GenericWebhookDTO();
        webhookDTO.license_plate = "ABC1234";
        webhookDTO.entry_time = java.time.OffsetDateTime.now().toString();
        EntryEventDTO dto = new EntryEventDTO(webhookDTO);
        when(parkingEventRepository.findByLicensePlateAndExitTimeIsNull(dto.licensePlate)).thenReturn(null);
        Sector sector = mock(Sector.class);
        when(sector.getMaxCapacity()).thenReturn(10);
        when(sectorRepository.findAll()).thenReturn(Arrays.asList(sector));
        when(parkingEventRepository.countBySectorAndExitTimeIsNull(sector)).thenReturn(2);
        ArgumentCaptor<ParkingEvent> captor = ArgumentCaptor.forClass(ParkingEvent.class);
        entryEventService.process(dto);
        verify(parkingEventRepository).save(captor.capture());
        ParkingEvent event = captor.getValue();
        assertEquals(dto.licensePlate, event.getLicensePlate());
        assertEquals(dto.entryTime, event.getEntryTime());
        assertEquals(sector, event.getSector());
        assertEquals(ParkingEventStatus.ENTRY, event.getStatus());
        assertEquals(BigDecimal.valueOf(0.9), event.getDynamicMultiplier());
    }
}
