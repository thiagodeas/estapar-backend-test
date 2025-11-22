package com.thiagoalves.estaparbackendtest.dtos.revenue;

import java.math.BigDecimal;

public class RevenueResponseDTO {

    public BigDecimal amount;
    public String currency = "BRL";
    public String timestamp;

    public RevenueResponseDTO(BigDecimal amount, String timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }
}
