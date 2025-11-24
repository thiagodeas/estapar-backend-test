package com.thiagoalves.estaparbackendtest.dtos.webhook;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class ExitEventDTO {

    public String licensePlate;
    public LocalDateTime exitTime;

    public ExitEventDTO(GenericWebhookDTO dto) {
        this.licensePlate = dto.license_plate;
        this.exitTime = OffsetDateTime.parse(dto.exit_time).toLocalDateTime();
    }
}
