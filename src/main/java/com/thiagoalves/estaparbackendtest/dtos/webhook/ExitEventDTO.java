package com.thiagoalves.estaparbackendtest.dtos.webhook;

import java.time.LocalDateTime;

public class ExitEventDTO {

    public String licensePlate;
    public LocalDateTime exitTime;

    public ExitEventDTO(GenericWebhookDTO dto) {
        this.licensePlate = dto.license_plate;
        this.exitTime = LocalDateTime.parse(dto.exit_time);
    }
}
