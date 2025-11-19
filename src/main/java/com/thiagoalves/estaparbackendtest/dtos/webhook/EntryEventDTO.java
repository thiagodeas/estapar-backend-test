package com.thiagoalves.estaparbackendtest.dtos.webhook;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class EntryEventDTO {

    public String licensePlate;

    public LocalDateTime entryTime; 

    public EntryEventDTO(GenericWebhookDTO dto) {
        this.licensePlate = dto.license_plate;
        this.entryTime = OffsetDateTime.parse(dto.entry_time).toLocalDateTime();
    }
}
