package com.thiagoalves.estaparbackendtest.services;

import com.thiagoalves.estaparbackendtest.dtos.GarageResponseDTO;
import com.thiagoalves.estaparbackendtest.dtos.SectorDTO;
import com.thiagoalves.estaparbackendtest.dtos.SpotDTO;
import com.thiagoalves.estaparbackendtest.models.Sector;
import com.thiagoalves.estaparbackendtest.models.Spot;
import com.thiagoalves.estaparbackendtest.repositories.SectorRepository;
import com.thiagoalves.estaparbackendtest.repositories.SpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

class GarageServiceTest {
    @Mock
    private SectorRepository sectorRepository;
    @Mock
    private SpotRepository spotRepository;
    @InjectMocks
    private GarageService garageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        garageService = new GarageService(sectorRepository, spotRepository);
    }

    @Test
    void loadGarageData_shouldHandleNullResponse() {
        GarageService serviceSpy = spy(garageService);
        doReturn(null).when(serviceSpy).getGarageResponse();
        serviceSpy.loadGarageData();
        verifyNoInteractions(sectorRepository);
        verifyNoInteractions(spotRepository);
    }

    @Test
    void loadGarageData_shouldSaveSectorsAndSpots() {
        GarageService serviceSpy = spy(garageService);
        GarageResponseDTO response = new GarageResponseDTO();
        SectorDTO sectorDTO = new SectorDTO();
        sectorDTO.sector = "A";
        sectorDTO.base_price = BigDecimal.valueOf(10);
        sectorDTO.max_capacity = 5;
        sectorDTO.open_hour = "08:00";
        sectorDTO.close_hour = "18:00";
        sectorDTO.duration_limit_minutes = 120;
        response.garage = List.of(sectorDTO);
        SpotDTO spotDTO = new SpotDTO();
        spotDTO.id = 1L;
        spotDTO.sector = "A";
        spotDTO.lat = 1.0;
        spotDTO.lng = 2.0;
        spotDTO.occupied = false;
        response.spots = List.of(spotDTO);
        doReturn(response).when(serviceSpy).getGarageResponse();
        Sector sector = mock(Sector.class);
        when(sectorRepository.findBySector("A")).thenReturn(null).thenReturn(sector);
        when(sectorRepository.save(any())).thenReturn(sector);
        when(spotRepository.save(any())).thenReturn(mock(Spot.class));
        serviceSpy.loadGarageData();
        verify(sectorRepository, times(1)).save(any(Sector.class));
        verify(spotRepository, times(1)).save(any(Spot.class));
    }

    @Test
    void saveSpot_shouldIgnoreSpotIfSectorNotFound() {
        SpotDTO spotDTO = new SpotDTO();
        spotDTO.id = 1L;
        spotDTO.sector = "B";
        spotDTO.lat = 1.0;
        spotDTO.lng = 2.0;
        spotDTO.occupied = true;
        when(sectorRepository.findBySector("B")).thenReturn(null);
        garageService.saveSpot(spotDTO);
        verify(spotRepository, never()).save(any());
    }
}
