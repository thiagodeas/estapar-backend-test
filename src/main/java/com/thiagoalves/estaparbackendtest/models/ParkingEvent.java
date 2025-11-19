package com.thiagoalves.estaparbackendtest.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thiagoalves.estaparbackendtest.models.enums.ParkingEventStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ParkingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licensePlate;

    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    private BigDecimal priceCalculated;
    private BigDecimal dynamicMultiplier;

    @Enumerated(EnumType.STRING)
    private ParkingEventStatus status;

    @ManyToOne
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @ManyToOne
    @JoinColumn(name = "spot_id")
    private Spot spot;

    public ParkingEvent() {}

    public ParkingEvent(
        String licensePlate,
        LocalDateTime entryTime,
        Sector sector,
        BigDecimal dynamicMultiplier,
        ParkingEventStatus status
    ) {
        this.licensePlate = licensePlate;
        this.entryTime = entryTime;
        this.sector = sector;
        this.dynamicMultiplier = dynamicMultiplier;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }

    public BigDecimal getPriceCalculated() { return priceCalculated; }
    public void setPriceCalculated(BigDecimal priceCalculated) { this.priceCalculated = priceCalculated; }

    public BigDecimal getDynamicMultiplier() { return dynamicMultiplier; }
    public void setDynamicMultiplier(BigDecimal dynamicMultiplier) { this.dynamicMultiplier = dynamicMultiplier; }

    public ParkingEventStatus getStatus() { return status; }
    public void setStatus(ParkingEventStatus status) { this.status = status; }

    public Sector getSector() { return sector; }
    public void setSector(Sector sector) { this.sector = sector; }

    public Spot getSpot() { return spot; }
    public void setSpot(Spot spot) { this.spot = spot; }
}
