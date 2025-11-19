package com.thiagoalves.estaparbackendtest.dtos.webhook;

public class ParkedEventDTO {

    public String licensePlate;
    public Double lat;
    public Double lng;

    public ParkedEventDTO(GenericWebhookDTO dto) {
        this.licensePlate = dto.license_plate;
        this.lat = dto.lat;
        this.lng = dto.lng;
    }
}
