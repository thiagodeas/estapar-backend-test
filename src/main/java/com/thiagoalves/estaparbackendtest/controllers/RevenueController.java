package com.thiagoalves.estaparbackendtest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.thiagoalves.estaparbackendtest.dtos.revenue.RevenueRequestDTO;
import com.thiagoalves.estaparbackendtest.dtos.revenue.RevenueResponseDTO;
import com.thiagoalves.estaparbackendtest.services.RevenueService;

@RestController
public class RevenueController {

    private final RevenueService revenueService;

    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @PostMapping("/revenue")
    public ResponseEntity<RevenueResponseDTO> getRevenue(
            @RequestBody RevenueRequestDTO request) {

        RevenueResponseDTO response = revenueService.calculate(request);
        return ResponseEntity.ok(response);
    }

}
