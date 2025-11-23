package com.thiagoalves.estaparbackendtest.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.thiagoalves.estaparbackendtest.models.Sector;
import com.thiagoalves.estaparbackendtest.models.Spot;
import com.thiagoalves.estaparbackendtest.repositories.SectorRepository;
import com.thiagoalves.estaparbackendtest.repositories.SpotRepository;

import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.thiagoalves.estaparbackendtest.dtos.GarageResponseDTO;
import com.thiagoalves.estaparbackendtest.dtos.SectorDTO;
import com.thiagoalves.estaparbackendtest.dtos.SpotDTO;


@Service
public class GarageService {

    private static final Logger logger = LoggerFactory.getLogger(GarageService.class);

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
        logger.info("Carregando configuração da garagem...");

        RestTemplate restTemplate = new RestTemplate();

        GarageResponseDTO response = restTemplate.getForObject(garageUrl, GarageResponseDTO.class);

        if (response == null) {
            logger.error("Erro: resposta nula do simulador.");
            return;
        }

        response.garage.forEach(this::saveSector);

        response.spots.forEach(this::saveSpot);

        logger.info("Configuração da garagem carregada com sucesso!");
    }

    private void saveSector(SectorDTO dto) {
        Sector sector = sectorRepository.findBySector(dto.sector);

        if (sector == null) {
            logger.info("Criando novo setor: {}", dto.sector);
            sector = new Sector();
            sector.setSector(dto.sector);
        } else {
            logger.info("Atualizando setor existente: {}", dto.sector);
        }

        sector.setBasePrice(dto.base_price);
        sector.setMaxCapacity(dto.max_capacity);
        sector.setOpenHour(LocalTime.parse(dto.open_hour));
        sector.setCloseHour(LocalTime.parse(dto.close_hour));
        sector.setDurationLimitMinutes(dto.duration_limit_minutes);

        sectorRepository.save(sector);
    }

    private void saveSpot(SpotDTO dto) {
        Sector sector = sectorRepository.findBySector(dto.sector);

        if (sector == null) {
            logger.warn("Spot ignorado: setor não encontrado: {}" + dto.sector);
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
