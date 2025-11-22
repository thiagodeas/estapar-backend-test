package com.thiagoalves.estaparbackendtest.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.thiagoalves.estaparbackendtest.services.GarageService;

@Component
public class StartupConfig implements CommandLineRunner {

    private final GarageService garageService;

    public StartupConfig(GarageService garageService) {
        this.garageService = garageService;
    }

    @Override
    public void run(String... args) {
        System.out.println("Aplicação iniciada. Buscando dados da garagem...");
        garageService.loadGarageData();
    }
}
