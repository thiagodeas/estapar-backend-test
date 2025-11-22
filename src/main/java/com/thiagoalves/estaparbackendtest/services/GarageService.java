package com.thiagoalves.estaparbackendtest.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.thiagoalves.estaparbackendtest.models.Sector;
import com.thiagoalves.estaparbackendtest.models.Spot;
import com.thiagoalves.estaparbackendtest.repositories.SectorRepository;
import com.thiagoalves.estaparbackendtest.repositories.SpotRepository;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Value;

import com.thiagoalves.estaparbackendtest.dtos.GarageResponseDTO;
import com.thiagoalves.estaparbackendtest.dtos.SectorDTO;
import com.thiagoalves.estaparbackendtest.dtos.SpotDTO;


@Service
public class GarageService {

    private final SectorRepository sectorRepository;
    private final SpotRepository spotRepository;

    @Value("${garage.url}")
    private String garageUrl;

    public GarageService(SectorRepository sectorRepository,
                         SpotRepository spotRepository) {
        this.sectorRepository = sectorRepository;
        this.spotRepository = spotRepository;
    }

    public void loadGarageData() {
        System.out.println("Carregando configuração da garagem...");

        RestTemplate restTemplate = new RestTemplate();

        GarageResponseDTO response = restTemplate.getForObject(garageUrl, GarageResponseDTO.class);

        if (response == null) {
            System.out.println("Erro: resposta nula do simulador.");
            return;
        }

        response.garage.forEach(this::saveSector);

        response.spots.forEach(this::saveSpot);

        System.out.println("Configuração da garagem carregada com sucesso!");
    }

    private void saveSector(SectorDTO dto) {
        Sector sector = sectorRepository.findBySector(dto.sector);

        if (sector == null) {
            sector = new Sector();
            sector.setSector(dto.sector);
        }

        sector.setBasePrice(dto.base_price);
        sector.setMaxCapacity(dto.max_capacity);
        sector.setOpenHour(LocalTime.parse(dto.open_hour));
        sector.setCloseHour(LocalTime.parse(dto.close_hour));
        sector.setDurationLimitMinutes(dto.duration_limit_minutes);

        sectorRepository.save(sector);
        System.out.println("Setor salvo/atualizado → " + dto.sector);
    }

    private void saveSpot(SpotDTO dto) {
        Sector sector = sectorRepository.findBySector(dto.sector);

        if (sector == null) {
            System.out.println("Spot ignorado: setor não encontrado → " + dto.sector);
            return;
        }

        Spot spot = new Spot(
                dto.id,
                sector,
                dto.lat,
                dto.lng,
                dto.occupied
        );

        spotRepository.save(spot);
    }
}
