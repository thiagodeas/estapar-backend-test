package com.thiagoalves.estaparbackendtest.models;

import java.math.BigDecimal;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Sector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sector;
    private BigDecimal basePrice;
    private Integer maxCapacity;
    private LocalTime openHour;
    private LocalTime closeHour;
    private Integer durationLimitMinutes;

    public Sector() {}

    public Sector(String sector, BigDecimal basePrice, Integer maxCapacity, LocalTime openHour, LocalTime closeHour, Integer durationLimitMinutes) {
        this.sector = sector;
        this.basePrice = basePrice;
        this.maxCapacity = maxCapacity;
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.durationLimitMinutes = durationLimitMinutes;
    }

    public Long getId() { return id; }
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }
    public LocalTime getOpenHour() { return openHour; }
    public void setOpenHour(LocalTime openHour) { this.openHour = openHour; }
    public LocalTime getCloseHour() { return closeHour; }
    public void setCloseHour(LocalTime closeHour) { this.closeHour = closeHour; }
    public Integer getDurationLimitMinutes() { return durationLimitMinutes; }
    public void setDurationLimitMinutes(Integer durationLimitMinutes) { this.durationLimitMinutes = durationLimitMinutes; }
}
