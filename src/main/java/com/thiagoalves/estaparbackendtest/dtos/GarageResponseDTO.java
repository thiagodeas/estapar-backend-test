package com.thiagoalves.estaparbackendtest.dtos;

import java.util.List;

public class GarageResponseDTO {

    private List<SectorDTO> garage;
    private List<SpotDTO> spots;

    public List<SectorDTO> getGarage() {
        return garage;
    }

    public void setGarage(List<SectorDTO> garage) {
        this.garage = garage;
    }

    public List<SpotDTO> getSpots() {
        return spots;
    }

    public void setSpots(List<SpotDTO> spots) {
        this.spots = spots;
    }
}
