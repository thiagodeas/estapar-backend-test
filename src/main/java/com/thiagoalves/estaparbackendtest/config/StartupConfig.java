package com.thiagoalves.estaparbackendtest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.thiagoalves.estaparbackendtest.services.GarageService;

@Component
public class StartupConfig implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupConfig.class);

    private final GarageService garageService;

    public StartupConfig(GarageService garageService) {
        this.garageService = garageService;
    }

    @Override
    public void run(String... args) {
        logger.info("Aplicação iniciada. Carregando configuração inicial da garagem...");
        garageService.loadGarageData();
    }
}
