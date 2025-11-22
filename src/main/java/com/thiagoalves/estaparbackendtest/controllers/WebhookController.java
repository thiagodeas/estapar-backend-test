package com.thiagoalves.estaparbackendtest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.thiagoalves.estaparbackendtest.dtos.webhook.EntryEventDTO;
import com.thiagoalves.estaparbackendtest.dtos.webhook.ExitEventDTO;
import com.thiagoalves.estaparbackendtest.dtos.webhook.GenericWebhookDTO;
import com.thiagoalves.estaparbackendtest.dtos.webhook.ParkedEventDTO;
import com.thiagoalves.estaparbackendtest.services.EntryEventService;
import com.thiagoalves.estaparbackendtest.services.ExitEventService;
import com.thiagoalves.estaparbackendtest.services.ParkedEventService;

@RestController
public class WebhookController {

    private final EntryEventService entryService;
    private final ParkedEventService parkedService;
    private final ExitEventService exitService;

    public WebhookController(
            EntryEventService entryService,
            ParkedEventService parkedService,
            ExitEventService exitService) {
        this.entryService = entryService;
        this.parkedService = parkedService;
        this.exitService = exitService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> receive(@RequestBody GenericWebhookDTO dto) {
        try {

            if ("ENTRY".equalsIgnoreCase(dto.event_type)) {
                entryService.process(new EntryEventDTO(dto));
            }

            else if ("PARKED".equalsIgnoreCase(dto.event_type)) {
                parkedService.process(new ParkedEventDTO(dto));
            }

            else if ("EXIT".equalsIgnoreCase(dto.event_type)) {
                exitService.process(new ExitEventDTO(dto));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
