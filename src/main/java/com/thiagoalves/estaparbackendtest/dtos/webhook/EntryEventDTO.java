package com.thiagoalves.estaparbackendtest.dtos.webhook;

import java.time.LocalDateTime;

public class EntryEventDTO {

    public String licensePlate;

    public LocalDateTime entryTime; 

    public EntryEventDTO(GenericWebhookDTO dto) {
        this.licensePlate = dto.license_plate;
        this.entryTime = LocalDateTime.parse(dto.entry_time);
    }
}
